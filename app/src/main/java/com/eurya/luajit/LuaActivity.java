package com.eurya.luajit;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;

public class LuaActivity extends Activity {

  private EditText mEditor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mEditor = findViewById(R.id.mEditor);
  }

  public void run(View view) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra("code", mEditor.getEditableText().toString());
    startActivity(intent);
  }
}
