package com.vaulterix.telegram_bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaulterix.telegram_bot.dto.AuthorizationUserDto;
import com.vaulterix.telegram_bot.exceptions.UploadFileException;
import com.vaulterix.telegram_bot.model.shares.CreateSharedLinkRequest;
import com.vaulterix.telegram_bot.request.CreateFolderRequest;
import com.vaulterix.telegram_bot.request.ShareRequest;
import com.vaulterix.telegram_bot.request.UploadKeyRequest;
import com.vaulterix.telegram_bot.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestService {
    @Value("${info.login}")
    private String name;
    @Value("${info.password}")
    private String password;
    @Value("${info.url}")
    private String urlPath;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper mapper = new ObjectMapper();


    public LoginResponse getLoginResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = urlPath + "api/login";
        AuthorizationUserDto userDto = new AuthorizationUserDto().setLogin(name).setPassword(password);
        HttpEntity<AuthorizationUserDto> entity = new HttpEntity<>(userDto, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        LoginResponse logHistoryResponse = null;
        try {
            logHistoryResponse = mapper.readValue(response, LoginResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return logHistoryResponse;
    }

    public FolderDataResponse createFolder(LoginResponse loginResponse, String userEmail) {
        FolderDataResponse rootFolder = getRootFolder(loginResponse);
        int indexTo = userEmail.indexOf("@");
        String substring = userEmail.substring(0, indexTo);
        CreateFolderRequest createFolderRequest = new CreateFolderRequest()
                .setFolderName(substring + "_" + OffsetDateTime.now().toInstant().toEpochMilli())
                .setParentId(rootFolder.getId())
                .setUserId(loginResponse.getUserId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", loginResponse.getAccessToken());
        String url = urlPath + "api/v1/folder/create";
        HttpEntity<CreateFolderRequest> entity = new HttpEntity<>(createFolderRequest, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        FolderDataResponse folderDataResponse;
        try {
            folderDataResponse = mapper.readValue(response, FolderDataResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return folderDataResponse;
    }

    public UploadKeyResponse getLoadKey(UploadKeyRequest uploadKeyRequest, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        String url = urlPath + "api/v1/file/upload-key";
        HttpEntity<UploadKeyRequest> entity = new HttpEntity<>(uploadKeyRequest, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        UploadKeyResponse keyResponse;
        try {
            keyResponse = mapper.readValue(response, UploadKeyResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
        return keyResponse;
    }

    public FileDataResponse compleateLoadFile(UploadKeyResponse uploadKey, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", accessToken);
        String url = urlPath + "api/v1/file/upload-complete?uploadKey=" + uploadKey.getUploadKey();
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        FileDataResponse fileDataResponse;
        try {
            fileDataResponse = mapper.readValue(response, FileDataResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return fileDataResponse;
    }

    public ShareResponse createShare(LoginResponse loginResponse, ShareRequest share) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", loginResponse.getAccessToken());
        String url = urlPath + "api/shares/create";
        HttpEntity<ShareRequest> entity = new HttpEntity<>(share, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        ShareResponse shareRequest;
        try {
            shareRequest = mapper.readValue(response, ShareResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return shareRequest;
    }

    public ShareInfoResponse createShareLink(LoginResponse loginResponse, CreateSharedLinkRequest createSharedLinkRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", loginResponse.getAccessToken());
        String url = urlPath + "api/shares/create-link";
        HttpEntity<CreateSharedLinkRequest> entity = new HttpEntity<>(createSharedLinkRequest, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        ShareInfoResponse shareRequest;
        try {
            shareRequest = mapper.readValue(response, ShareInfoResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return shareRequest;
    }

    public FolderDataResponse getRootFolder(LoginResponse loginResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", loginResponse.getAccessToken());
        String url = urlPath + "api/v1/folder/root";
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        FolderDataResponse folderDataResponse;
        try {
            folderDataResponse = mapper.readValue(response, FolderDataResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return folderDataResponse;
    }

    public UploadChunkResponse loadChunk(byte[] fileInByte, UploadKeyResponse uploadKey, int packageNumber, int fromByte, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", accessToken);
        String url = urlPath + "api/v1/storage-file/upload-chunk";
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.put("part", Collections.singletonList(fileInByte));
        map.put("uploadKey", Collections.singletonList(uploadKey.getUploadKey()));
        map.put("packageNumber", Collections.singletonList(packageNumber));
        map.put("fromByte", Collections.singletonList(fromByte));
        map.put("toByte", Collections.singletonList(fileInByte.length));
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
        UploadChunkResponse chunkResponse;
        try {
            chunkResponse = mapper.readValue(response, UploadChunkResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return null;
        }
        return chunkResponse;
    }

    public boolean checkEmailCreateInVaulterix(String email) {
        String accessToken = getLoginResponse().getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        String url = urlPath + "api/users/email/" + email;
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        UserSaveResponse userSaveResponse;
        try {
            userSaveResponse = mapper.readValue(response, UserSaveResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed get or parse log history from log collector");
            return false;
        }
        return userSaveResponse.getUser().getRolesId().contains("EXTERNAL_USER") ? false : true;
    }
}
