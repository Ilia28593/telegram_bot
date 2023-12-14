package com.vaulterix.telegram_bot.service;

import com.vaulterix.telegram_bot.exceptions.UploadFileException;
import com.vaulterix.telegram_bot.model.User;
import com.vaulterix.telegram_bot.model.shares.CreateSharedLinkRequest;
import com.vaulterix.telegram_bot.model.shares.StorageEntityModel;
import com.vaulterix.telegram_bot.model.shares.UserProtectionRequest;
import com.vaulterix.telegram_bot.repository.UserRepository;
import com.vaulterix.telegram_bot.request.ShareRequest;
import com.vaulterix.telegram_bot.request.UploadKeyRequest;
import com.vaulterix.telegram_bot.request.UserInfoRequest;
import com.vaulterix.telegram_bot.request.UserPermissionInfoRequest;
import com.vaulterix.telegram_bot.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TGService implements BotService {
    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final UserRepository userRepository;
    private final RequestService requestService;

    public void clean(long chatId) {
        Optional<User> user = userRepository.findByChatId(chatId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        }
    }

    public String shareDoc(Message telegramMessage, long chatId) {
        LoginResponse loginResponse = requestService.getLoginResponse();
        var telegramDoc = telegramMessage.getDocument();
        if (telegramDoc.getFileSize() > 20971520)
            return "Размер файла привышает, пожалуйста загрузите не привышающий 20 мб";
        var fileId = telegramDoc.getFileId();
        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            String folderId = getFolderId(chatId, loginResponse);
            sendFile(response, folderId, loginResponse.getAccessToken(), telegramDoc.getFileName(), telegramDoc.getFileSize());
            return "Фаил успешно загружен:\n" + telegramDoc.getFileName();
        } else {
            throw new UploadFileException("Bad response from telegram service:\n" + response);
        }
    }

    @Override
    public void consumeTextMessageUpdates(Update update) {

    }

    @Override
    public void consumeDocMessageUpdates(Update update) {

    }

    @Override
    public String consumePhotoMessageUpdates(Update update) {
        LoginResponse loginResponse = requestService.getLoginResponse();
        var chatId = update.getMessage().getChatId();
        var photoSizeCount = update.getMessage().getPhoto().size();
        if (photoSizeCount > 20971520) return "Размер файла привышает, пожалуйста загрузите не привышающий 20 мб";
        var photoIndex = photoSizeCount > 1 ? update.getMessage().getPhoto().size() - 1 : 0;
        var telegramPhoto = update.getMessage().getPhoto().get(photoIndex);
        var fileId = telegramPhoto.getFileId();
        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            String fileName = List.of(getFilePath(response).split("/")).get(1);
            String folderId = getFolderId(chatId, loginResponse);
            sendFile(response, folderId, loginResponse.getAccessToken(), fileName, update.getMessage().getPhoto().size());
            return "Картинка успешно загружена.";
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }

    }

    private String getFolderId(long chatId, LoginResponse loginResponse) {
        User user = userRepository.findByChatIdAndStatusFalse(chatId).get();
        if (user.getPart() == 2) {
            user.setPart(user.getPart() + 1);
        }
        if (StringUtils.isBlank(user.getFolderId())) {
            FolderDataResponse folder = requestService.createFolder(loginResponse, user.getUserEmail());
            user.setFolderId(folder.getId());
            userRepository.save(user);
            return folder.getId();
        } else {
            return user.getFolderId();
        }
    }

    public String createLinkByShares(long chatId) {
        User user = userRepository.findByChatIdAndStatusFalse(chatId).get();
        LoginResponse loginResponse = requestService.getLoginResponse();
        StringBuilder emailLinc = new StringBuilder();
        user.getSharingEmail().forEach(email -> {
            ShareRequest shareRequest = getShareRequest(user.getFolderId(), email);
            ShareResponse shareByIdFile = requestService.createShare(loginResponse, shareRequest);
            if (StringUtils.isBlank(shareByIdFile.getLink())) {
                emailLinc.append(email + ":\n" + createAnonimLink(loginResponse, shareRequest) + "\n");
            } else {
                emailLinc.append(email + ":\n" + shareByIdFile.getLink() + "\n");
            }
        });
        user.setStatus(true);
        userRepository.save(user);
        return emailLinc.toString();
    }

    private String createAnonimLink(LoginResponse loginResponse, ShareRequest shareRequest) {
        CreateSharedLinkRequest createSharedLinkRequest = new CreateSharedLinkRequest()
                .setEntity(shareRequest.getEntity())
                .setUserPermissionInfo(shareRequest.getUserPermissionInfo());
        ShareInfoResponse shareLink = requestService.createShareLink(loginResponse, createSharedLinkRequest);
        return shareLink.getLink();
    }

    private ShareRequest getShareRequest(String folderId, String email) {
        StorageEntityModel file = new StorageEntityModel()
                .setId(folderId)
                .setType("DIRECTORY");
        UserProtectionRequest userProtectionRequest = new UserProtectionRequest()
                .setAllowPrintingDownloaded(false)
                .setUserPasswordEnable(false)
                .setOwnerPassword("");
        UserInfoRequest userInfoRequest = new UserInfoRequest().setEmail(email);
        UserPermissionInfoRequest userPermissionInfoRequest = new UserPermissionInfoRequest()
                .setAddMetadataFile(false)
                .setAddMetadataPdfFile(false)
                .setAllowCopy(false)
                .setAllowEditFile(false)
                .setAllowPrinting(true)
                .setAllowView(true)
                .setAllowViewXlsx(false)
                .setDateTimeOnWaterMark(true)
                .setDownloadPlainFile(false)
                .setDownloadWatermarkFile(false)
                .setExpirationDate(OffsetDateTime.now(Clock.systemUTC()).plusDays(10).toZonedDateTime().toString())
                .setIpAddressOnWaterMark(true)
                .setNdaId("")
                .setNumberOfDownloads(10)
                .setNumberOfDownloads(10)
                .setPassword(null)
                .setPermissionLevel("write")
                .setPrincipal(userInfoRequest)
                .setRestrictNumberOfDownloads(false)
                .setSecureView(false)
                .setShowNda(false)
                .setUploadFileAllowed(false)
                .setUserProtection(userProtectionRequest)
                .setDownloadWatermarkFile(false)
                .setWaterMarkSenderEmail(true)
                .setWaterMarkReceiverEmail(true)
                .setWaterMarkText("Telegram bot");

        return new ShareRequest().setEntity(file).setUserPermissionInfo(userPermissionInfoRequest);
    }

    private byte[] downloadFile(String filePath) {
        var fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private FileDataResponse sendFile(ResponseEntity<String> response, String folderId, String accessToken, String fileName, int fileSize) {
        var filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        UploadKeyRequest uploadKeyRequest = new UploadKeyRequest()
                .setFileName(fileName)
                .setFileSize(fileSize)
                .setFolderId(folderId)
                .setToRewrite(false);
        UploadKeyResponse uploadKey = requestService.getLoadKey(uploadKeyRequest, accessToken);
        int length = fileInByte.length;
        int to = 5242880;
        int from = 0;
        int packageNumber = 1;
        while (length > 5242880) {
            byte[] data = java.util.Arrays.copyOfRange(fileInByte, from, to);
            requestService.loadChunk(data, uploadKey, packageNumber, 0, accessToken);
            length = -to;
            from = to + 1;
            to = +to;
            packageNumber = +1;
        }
        requestService.loadChunk(fileInByte, uploadKey, packageNumber, 0, accessToken);
        return requestService.compleateLoadFile(uploadKey, accessToken);
    }

    private String getFilePath(ResponseEntity<String> response) {
        var jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);
        return restTemplate.exchange(fileInfoUri, HttpMethod.GET, request, String.class, token, fileId);
    }

    public String sveUserResponse(String email, long chatId) {
        Optional<User> userOptional = userRepository.findByChatIdAndStatusFalse(chatId);
        if (userOptional.isEmpty()) {
            try {
                if (requestService.checkEmailCreateInVaulterix(email)) {
                    createUser(email, chatId);
                    return "е-mail:" + email + " принят.\nВведите почтовые адреса получателей через запятую. И нажмите далее.";
                }
            } catch (Exception e) {
                return email + ": данный  е-mail не зарегистрирован в Vaulterix.";
            }
            return email + ": данный  е-mail не зарегистрирован в Vaulterix.";
        } else {
            User user = userOptional.get();
            if (user.getSharingEmail().isEmpty()) {
                Set<String> users = new HashSet<>();
                users.add(email);
                user.setSharingEmail(users);
            } else {
                user.getSharingEmail().add(email);
            }
            if (user.getPart() == 1) {
                user.setPart(user.getPart() + 1);
            }
            User save = userRepository.save(userOptional.get());
            StringBuilder emailList = new StringBuilder();
            save.getSharingEmail().forEach(e -> emailList.append("\n" + e));
            return "Делимся файлами с получателями:" + emailList + "\nДобавьте еще получателей или нажмите далее";
        }
    }

    public Optional<User> getUser(Long chatId) {
        return userRepository.findByChatIdAndStatusFalse(chatId);
    }

    public void createUser(String email, Long chatId) {
        User user = new User()
                .setUserEmail(email)
                .setChatId(chatId)
                .setDateCreateRequest(LocalDateTime.now())
                .setPart(1);
        userRepository.save(user);
    }
}
