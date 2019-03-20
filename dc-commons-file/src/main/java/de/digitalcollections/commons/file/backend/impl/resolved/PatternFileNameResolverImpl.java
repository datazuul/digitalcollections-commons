package de.digitalcollections.commons.file.backend.impl.resolved;

import de.digitalcollections.model.api.identifiable.resource.MimeType;
import de.digitalcollections.model.api.identifiable.resource.exceptions.ResourceIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternFileNameResolverImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(PatternFileNameResolverImpl.class);

  private Pattern compiledPattern;

  private String pattern;

  private List<String> substitutions;

  public PatternFileNameResolverImpl() {
  }

  public PatternFileNameResolverImpl(String regex, String replacement) {
    this.pattern = regex;
    this.compiledPattern = Pattern.compile(regex);
    this.substitutions = Collections.singletonList(replacement.replace("~", System.getProperty("user.home")));
  }

  public Set<Path> getPaths() throws ResourceIOException {
    return substitutions.stream()
        .filter(s -> s.startsWith("file:"))
        .map(p -> Paths.get(p))
        .collect(Collectors.toSet());
  }

  /**
   * Resolve the identifier to java.nio.Path objects.
   *
   * @param identifier file identifier/resolving key
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsString for key fails
   */
  public List<Path> getPaths(String identifier) throws ResourceIOException {
    return getUrisAsString(identifier).stream()
        .filter(s -> s.startsWith("file:"))
        .map(Paths::get)
        .collect(Collectors.toList());
  }

  /**
   * Return resolved Paths that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsString for key fails
   */
  public List<Path> getPaths(String identifier, MimeType mimeType) throws ResourceIOException {
    return getPaths(identifier).stream()
        .filter(p -> mimeType.matches(MimeType.fromFilename(p.toString())))
        .collect(Collectors.toList());
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
    this.compiledPattern = Pattern.compile(pattern);
  }

  public List<String> getSubstitutions() {
    return substitutions;
  }

  public void setSubstitutions(List<String> substitutions) {
    this.substitutions = substitutions.stream()
        .map(s -> s.replace("~", System.getProperty("user.home")))
        .collect(Collectors.toList());
  }

  /**
   * Resolve the identifier to URI objects.
   *
   * @param identifier file identifier/resolving key
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsString for key fails
   */
  public List<URI> getUris(String identifier) throws ResourceIOException {
    return getUrisAsString(identifier).stream()
        .map(URI::create)
        .collect(Collectors.toList());
  }

  /**
   * Return resolved URIs that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsString for key fails
   */
  public List<URI> getUris(String identifier, MimeType mimeType) throws ResourceIOException {
    final List<URI> uris = getUris(identifier);
    return uris.stream()
        .filter(u -> (mimeType.matches(MimeType.fromURI(u)) || MimeType.fromURI(u) == null))
        .collect(Collectors.toList());
  }

  public List<String> getUrisAsString(String identifier) {
    Matcher matcher = this.compiledPattern.matcher(identifier);
    return this.substitutions.stream()
        .map(matcher::replaceAll)
        .collect(Collectors.toList());
  }

  /**
   * Return resolved strings that match the given MIME type.
   *
   * @param identifier file identifier/resolving key
   * @param mimeType target mimetype (resolving subkey)
   * @return list of resolved file uris
   * @throws ResourceIOException in case getUrisAsString for key fails
   */
  public List<String> getUrisAsString(String identifier, MimeType mimeType) throws ResourceIOException {
    return getUrisAsString(identifier).stream()
        .filter(s -> mimeType.matches(MimeType.fromFilename(s)))
        .collect(Collectors.toList());
  }

  public Boolean isResolvable(String identifier) {
    Boolean b = this.compiledPattern.matcher(identifier).matches();
    LOGGER.debug("Matching " + identifier + " against " + this.pattern + " is " + b);
    return b;
  }
}