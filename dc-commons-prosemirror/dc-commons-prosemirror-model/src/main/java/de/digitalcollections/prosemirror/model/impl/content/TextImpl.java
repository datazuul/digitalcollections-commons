package de.digitalcollections.prosemirror.model.impl.content;

import de.digitalcollections.prosemirror.model.api.content.Mark;
import de.digitalcollections.prosemirror.model.api.content.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextImpl extends ContentImpl implements Text {

  private String text;
  private List<Mark> marks;

  public TextImpl() {
  }

  public TextImpl(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TextImpl)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TextImpl text1 = (TextImpl) o;
    return Objects.equals(text, text1.text);
  }

  @Override
  public List<Mark> getMarks() {
    return marks;
  }

  @Override
  public void setMarks(List<Mark> marks) {
    this.marks = marks;
  }

  @Override
  public void addMark(Mark mark) {
    if ( marks == null ) {
      marks = new ArrayList<>();
    }

    marks.add(mark);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), text, marks);
  }

  @Override
  public String toString() {
    return "TextImpl{"
        + "text='" + text + "\', "
        + "marks=" + marks + ","
        + "contents=" + contents
        + '}';
  }
}
