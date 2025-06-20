package andrehsvictor.memorix.common.minio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinioService Tests")
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private MinioService minioService;

    private String testBucketName;
    private String testEndpoint;
    private Map<String, String> testMetadata;

    @BeforeEach
    void setUp() {
        testBucketName = "test-bucket";
        testEndpoint = "https://minio.example.com";
        testMetadata = Map.of("userId", "123", "type", "profile");

        when(minioProperties.getBucketName()).thenReturn(testBucketName);
        when(minioProperties.getEndpoint()).thenReturn(testEndpoint);
    }

    @Test
    @DisplayName("Should upload file successfully")
    void upload_ShouldReturnFileUrl_WhenFileUploadSuccessful() throws Exception {
        // Given
        String originalFilename = "test.jpg";
        String contentType = "image/jpeg";
        long fileSize = 1024L;
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        // When
        String result = minioService.upload(multipartFile, testMetadata);

        // Then
        assertThat(result).startsWith(testEndpoint + "/" + testBucketName + "/");
        assertThat(result).endsWith("-" + originalFilename);

        ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(putObjectArgsCaptor.capture());
    }

    @Test
    @DisplayName("Should throw RuntimeException when upload fails")
    void upload_ShouldThrowRuntimeException_WhenUploadFails() throws Exception {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        doThrow(new RuntimeException("Minio error")).when(minioClient).putObject(any(PutObjectArgs.class));

        // When & Then
        assertThatThrownBy(() -> minioService.upload(multipartFile, testMetadata))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to upload file to Minio");
    }

    @Test
    @DisplayName("Should delete file by URL successfully when URL is valid")
    void delete_ShouldDeleteFile_WhenUrlIsValid() throws Exception {
        // Given
        String validUrl = testEndpoint + "/" + testBucketName + "/123456-test.jpg";

        // When
        minioService.delete(validUrl);

        // Then
        ArgumentCaptor<RemoveObjectArgs> removeObjectArgsCaptor = ArgumentCaptor.forClass(RemoveObjectArgs.class);
        verify(minioClient).removeObject(removeObjectArgsCaptor.capture());
    }

    @Test
    @DisplayName("Should skip deletion when URL is invalid")
    void delete_ShouldSkipDeletion_WhenUrlIsInvalid() throws Exception {
        // Given
        String invalidUrl = "https://other-service.com/file.jpg";

        // When
        minioService.delete(invalidUrl);

        // Then
        verify(minioClient, never()).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should skip deletion when URL is null")
    void delete_ShouldSkipDeletion_WhenUrlIsNull() throws Exception {
        // When
        minioService.delete(null);

        // Then
        verify(minioClient, never()).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when delete fails")
    void delete_ShouldThrowRuntimeException_WhenDeleteFails() throws Exception {
        // Given
        String validUrl = testEndpoint + "/" + testBucketName + "/test.jpg";
        doThrow(new RuntimeException("Minio delete error")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When & Then
        assertThatThrownBy(() -> minioService.delete(validUrl))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to delete file from Minio");
    }

    @Test
    @DisplayName("Should throw RuntimeException when delete by metadata fails")
    void deleteByMetadata_ShouldThrowRuntimeException_WhenDeleteFails() throws Exception {
        // Given
        doThrow(new RuntimeException("Minio list error")).when(minioClient).listObjects(any());

        // When & Then
        assertThatThrownBy(() -> minioService.deleteByMetadata(testMetadata))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to delete files with metadata from Minio");
    }
}
