package de.digitalcollections.commons.file.backend.impl;

import de.digitalcollections.commons.file.business.api.FileResourceService;
import de.digitalcollections.commons.file.config.SpringConfigCommonsFile;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceNotFoundException;
import de.digitalcollections.model.impl.identifiable.resource.FileResourceImpl;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringConfigCommonsFile.class})
@ActiveProfiles("TEST")
public class FileResourceRepositoryImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceRepositoryImplTest.class);
  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private FileResourceRepositoryImpl resourceRepository;

  @Autowired
  private FileResourceService resourceService;

  @Test
  public void assertNonReadableFile() {
    assertThatThrownBy(() -> {
      FileResource nonReadableResource = new FileResourceImpl();
      nonReadableResource.setUri(new URI("file:/root/.profile"));
      nonReadableResource.setMimeType(MimeType.MIME_WILDCARD);
      resourceRepository.assertReadability(nonReadableResource);
    }).isInstanceOf(Exception.class);
  }

  @Test
  public void assertNonexistingFile() {
    assertThatThrownBy(() -> {
      FileResource nonexistingResource = new FileResourceImpl();
      nonexistingResource.setUri(new URI("file:/tmp/nonexistant"));
      nonexistingResource.setMimeType(MimeType.MIME_WILDCARD);
      resourceRepository.assertReadability(nonexistingResource);
    }).isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  public void assertZeroByteFile() {
    assertThatThrownBy(() -> {
      FileResource zeroByteLengthResource = new FileResourceImpl();
      zeroByteLengthResource.setUri(new URI("file:/proc/uptime"));
      zeroByteLengthResource.setMimeType(MimeType.MIME_WILDCARD);
      resourceRepository.assertReadability(zeroByteLengthResource);
    }).isInstanceOf(ResourceIOException.class);
  }

  @Test
  public void testCreate() throws Exception {
    // test resolved
    String identifier = "bsb00001000";
    FileResource resource = resourceRepository.create(identifier, MimeType.MIME_APPLICATION_XML);
    URI expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);

    // test referenced
    identifier = "bsb00001000";
    resource = resourceRepository.create(identifier, MimeType.MIME_APPLICATION_XML);
    expResult = URI.create("http://rest.digitale-sammlungen.de/data/bsb00001000.xml");
    result = resource.getUri();
    assertThat(expResult).isEqualTo(result);
  }

  @Test
  public void testCreateUri() throws Exception {
    FileResource resource = resourceRepository.create("a30cf362-5992-4f5a-8de0-61938134e721", MimeType.MIME_APPLICATION_XML);
    URI result = resource.getUri();
    URI expResult = URI.create("file:///src/test/resources/repository/dico/a30c/f362/5992/4f5a/8de0/6193/8134/e721/a30cf362-5992-4f5a-8de0-61938134e721.xml");
    assertThat(expResult).isEqualTo(result);
  }

  @Test
  public void testFind() throws Exception {
    FileResource resource = resourceRepository.find("snafu", MimeType.MIME_APPLICATION_XML);

    URI expResult = URI.create("classpath:/snafu.xml");
    URI result = resource.getUri();
    assertThat(expResult).isEqualTo(result);

    long expSize = 71;
    long size = resource.getSizeInBytes();
    assertThat(expSize).isEqualTo(size);

    LocalDateTime lastModified = resource.getLastModified();
    assertThat(lastModified.getDayOfMonth() > 0).isTrue();
  }

  @Test
  public void testFindMimeWildcard() throws Exception {
    FileResource res = resourceRepository.find("snafu", MimeType.MIME_WILDCARD);
    assertThat(res.getUri()).isEqualTo(URI.create("classpath:/snafu.xml"));
  }

  @Test
  public void testGetUrisAsStringsForCustomResolver() throws Exception {
    List<String> urisAsString = resourceRepository.getUrisAsString("identifier_resolved_by_custom_resolver");
    assertThat(urisAsString).isNotEmpty();
  }

  public void testReadXMLDocument() throws ResourceIOException, ResourceNotFoundException {
    FileResource fileResource = resourceRepository.create("snafu", MimeType.MIME_APPLICATION_XML);
    Document document = resourceService.getAsDocument(fileResource);
    Node rootElement = document.getElementsByTagName("rootElement").item(0);
    String textContent = rootElement.getTextContent();
    assertThat("SNAFU").isEqualTo(textContent);
  }
}