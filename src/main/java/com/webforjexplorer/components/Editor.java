package com.webforjexplorer.components;

import com.google.gson.JsonObject;
import com.webforj.annotation.Attribute;
import com.webforj.annotation.InlineJavaScript;
import com.webforj.component.element.ElementComposite;
import com.webforj.component.element.PropertyDescriptor;
import com.webforj.component.element.annotation.NodeName;
import com.webforj.concern.HasSize;
import com.webforj.concern.HasValue;
import com.webforj.data.event.ValueChangeEvent;
import com.webforj.dispatcher.EventListener;
import com.webforj.dispatcher.ListenerRegistration;

@InlineJavaScript(value = "import * as heyWebComponentsmonacoEditor from 'https://esm.run/@hey-web-components/monaco-editor'", attributes = {
    @Attribute(name = "type", value = "module")
})
@NodeName("hey-monaco-editor")
public class Editor extends ElementComposite implements HasSize<Editor>, HasValue<Editor, String> {

  PropertyDescriptor<String> valueDescriptor = PropertyDescriptor.property("value", "");
  PropertyDescriptor<String> languageDescriptor = PropertyDescriptor.property("language", "");
  PropertyDescriptor<JsonObject> options = PropertyDescriptor.property("options", new JsonObject());

  public Editor() {
    var initOptions = new JsonObject();
    // initOptions.addProperty("theme", "vs-dark");
    initOptions.addProperty("readOnly", true);

    set(this.options, initOptions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Editor setValue(String value) {
    set(valueDescriptor, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValue() {
    return get(valueDescriptor);
  }

  /**
   * Sets the language of the editor.
   *
   * @param language the language to set
   * @return this editor instance
   */
  public Editor setLanguage(String language) {
    set(languageDescriptor, language);
    return this;
  }

  /**
   * Gets the language of the editor.
   *
   * @return the language of the editor
   */
  public String getLanguage() {
    return get(languageDescriptor);
  }

  @Override
  public ListenerRegistration<ValueChangeEvent<String>> addValueChangeListener(
      EventListener<ValueChangeEvent<String>> listener) {
    // no-op
    // can be implemented if needed
    // currently, the editor is read-only
    return null;
  }
}
