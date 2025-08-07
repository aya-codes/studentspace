package com.ayacodes.studentspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DropboxUploader {
    String appKey = "ew1c8btx6tabn9d";
    String appSecret = "n71br5p8no3kws8";
***REMOVED***
    String tokenUrl = "https://api.dropbox.com/oauth2/token";

    public void archiveAndUploadChat(File file) throws IOException {
        String accessToken = getNewAccessToken();
        String dropboxUploadUrl = "https://content.dropboxapi.com/2/files/upload";

        HttpPost post = new HttpPost(dropboxUploadUrl);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Dropbox-API-Arg", "{\"path\": \"" +
                file.getName() + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}");
        post.setHeader("Content-Type", "application/octet-stream");

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = fileInputStream.readAllBytes();
            post.setEntity(new ByteArrayEntity(fileBytes));
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                    .lines().collect(Collectors.joining("\n"));

            if (statusCode == 200) {
                System.out.println("Upload successful: " + responseBody);
            } else {
                System.err.println("Upload failed with status: " + statusCode);
                System.err.println("Response: " + responseBody);
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNewAccessToken() throws IOException {
        HttpPost post = new HttpPost(tokenUrl);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "refresh_token"));
        params.add(new BasicNameValuePair("refresh_token", refreshToken));
        post.setEntity(new UrlEncodedFormEntity(params));

        String auth = Base64.getEncoder().encodeToString((appKey + ":" + appSecret).getBytes());
        post.setHeader("Authorization", "Basic " + auth);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            int status = response.getStatusLine().getStatusCode();
            String responseBody = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                    .lines().collect(Collectors.joining("\n"));

            if (status == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(responseBody);
                return json.get("access_token").asText();
            } else {
                throw new IOException("Failed to get access token. Status: " + status + "\n" + responseBody);
            }
        }
    }
}
