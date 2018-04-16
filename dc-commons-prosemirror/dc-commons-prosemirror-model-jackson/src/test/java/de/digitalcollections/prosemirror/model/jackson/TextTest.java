package de.digitalcollections.prosemirror.model.jackson;

import de.digitalcollections.prosemirror.model.api.content.Mark;
import de.digitalcollections.prosemirror.model.api.content.Text;
import de.digitalcollections.prosemirror.model.impl.content.MarkImpl;
import de.digitalcollections.prosemirror.model.impl.content.TextImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextTest extends BaseProseMirrorObjectMapperTest {

  @Test
  public void testDefaultConstructor() throws Exception {
    Text text = new TextImpl();

    checkSerializeDeserialize(text);
  }

  @Test
  public void testWithConstructor() throws Exception {
    Text text = new TextImpl("MDZ");

    checkSerializeDeserialize(text);
  }

  @Test
  public void testWithSetters() throws Exception {
    Text text = new TextImpl();
    text.setText("MDZ");

    checkSerializeDeserialize(text);
  }

  @Test
  public void testOnlyMarks() throws Exception {
    Text text = new TextImpl();
    text.addMark(new MarkImpl("strong"));
    text.addMark(new MarkImpl("em"));

    checkSerializeDeserialize(text);
  }

  @Test
  public void testConstructorAndMarks() throws Exception {
    Text text = new TextImpl("MDZ");
    text.addMark(new MarkImpl("strong"));
    text.addMark(new MarkImpl("em"));
    checkSerializeDeserialize(text);
  }

  @Test
  public void testDeserializationWithEmptyText() throws Exception {
    String jsonString = "{\n"
        + "          \"type\": \"text\"\n"
        + "        }";

    Text text = mapper.readValue(jsonString, Text.class);
    assertThat(text).isNotNull();
    assertThat(text.getText()).isNull();
  }


  @Test
  public void testDeserializationWithText() throws Exception {
    String jsonString = "{\n"
        + "          \"type\": \"text\",\n"
        + "          \"text\": \"Impressum\"\n"
        + "        }";

    Text text = mapper.readValue(jsonString, Text.class);
    assertThat(text).isNotNull();
    assertThat(text.getText()).isEqualTo("Impressum");
    assertThat(text.getMarks()).isNull();
  }

  @Test
  @Disabled("This is not valid JSON!")
  public void testDeserializationWithMarksOnly() throws Exception {
    String jsonString = "{\n"
        + "          \"type\": \"text\",\n"
        + "           \"marks\": [\n"
        + "             {\n"
        + "                \"type\": \"strong\",\n"
        + "                \"type\": \"em\"\n"
        + "             }\n"
        + "            ]\n"
        + "        }";

    Text text = mapper.readValue(jsonString, Text.class);
    assertThat(text).isNotNull();
    assertThat(text.getText()).isNull();

    Mark strong = new MarkImpl("strong");
    Mark em = new MarkImpl("em");

    System.out.println("text.getMarks()=" + text.getMarks());

    assertThat(text.getMarks()).containsExactly(strong,em);
  }

  @Test
  @Disabled("This is not valid JSON!")
  public void testDeserializationWithTextAndMarks() throws Exception {
    String jsonString = "{\n"
        + "      \"type\": \"text\",\n"
        + "      \"marks\": [\n"
        + "        {\n"
        + "          \"type\": \"strong\",\n"
        + "          \"type\": \"em\"\n"
        + "        }\n"
        + "      ],\n"
        + "      \"text\": \"Fax:\"\n"
        + "    }";

    Text text = mapper.readValue(jsonString, Text.class);
    assertThat(text).isNotNull();
    assertThat(text.getText()).isEqualTo("Fax:");
    Mark strong = new MarkImpl("strong");
    Mark em = new MarkImpl("em");
    assertThat(text.getMarks()).containsExactly(strong,em);
  }


}
