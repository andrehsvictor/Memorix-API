package andrehsvictor.memorix.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileUtil Tests")
class FileUtilTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @InjectMocks
    private FileUtil fileUtil;

    private String testPath;
    private String testContent;

    @BeforeEach
    void setUp() {
        testPath = "classpath:test-file.txt";
        testContent = "Hello, World!";
    }

    @Test
    @DisplayName("Should read file as string successfully")
    void readFileAsString_ShouldReturnContent_WhenFileExists() throws IOException {
        // Given
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(testContent.getBytes()));

        // When
        String result = fileUtil.readFileAsString(testPath);

        // Then
        assertThat(result).isEqualTo(testContent);
    }

    @Test
    @DisplayName("Should throw RuntimeException when file does not exist")
    void readFileAsString_ShouldThrowRuntimeException_WhenFileDoesNotExist() {
        // Given
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> fileUtil.readFileAsString(testPath))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File not found: " + testPath);
    }

    @Test
    @DisplayName("Should throw RuntimeException when IOException occurs")
    void readFileAsString_ShouldThrowRuntimeException_WhenIOExceptionOccurs() throws IOException {
        // Given
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("IO Error"));

        // When & Then
        assertThatThrownBy(() -> fileUtil.readFileAsString(testPath))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error reading file: " + testPath);
    }

    @Test
    @DisplayName("Should process template with placeholders successfully")
    void processTemplate_ShouldReplaceePlaceholders_WhenTemplateAndPlaceholdersProvided() throws IOException {
        // Given
        String template = "Hello {{name}}, welcome to {{application}}!";
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(template.getBytes()));

        Map<String, String> placeholders = Map.of(
                "name", "John",
                "application", "Memorix"
        );

        // When
        String result = fileUtil.processTemplate(testPath, placeholders);

        // Then
        assertThat(result).isEqualTo("Hello John, welcome to Memorix!");
    }

    @Test
    @DisplayName("Should handle null placeholder values")
    void processTemplate_ShouldReplaceWithEmptyString_WhenPlaceholderValueIsNull() throws IOException {
        // Given
        String template = "Hello {{name}}, {{greeting}}!";
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(template.getBytes()));

        Map<String, String> placeholders = Map.of(
                "name", "John",
                "greeting", null
        );

        // When
        String result = fileUtil.processTemplate(testPath, placeholders);

        // Then
        assertThat(result).isEqualTo("Hello John, !");
    }

    @Test
    @DisplayName("Should leave unmatched placeholders unchanged")
    void processTemplate_ShouldLeaveUnmatchedPlaceholders_WhenNotProvided() throws IOException {
        // Given
        String template = "Hello {{name}}, welcome to {{application}}!";
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(template.getBytes()));

        Map<String, String> placeholders = Map.of("name", "John");

        // When
        String result = fileUtil.processTemplate(testPath, placeholders);

        // Then
        assertThat(result).isEqualTo("Hello John, welcome to {{application}}!");
    }

    @Test
    @DisplayName("Should handle empty placeholders map")
    void processTemplate_ShouldReturnOriginalTemplate_WhenPlaceholdersEmpty() throws IOException {
        // Given
        String template = "Hello {{name}}, welcome to {{application}}!";
        when(resourceLoader.getResource(testPath)).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(template.getBytes()));

        Map<String, String> placeholders = Map.of();

        // When
        String result = fileUtil.processTemplate(testPath, placeholders);

        // Then
        assertThat(result).isEqualTo(template);
    }
}
