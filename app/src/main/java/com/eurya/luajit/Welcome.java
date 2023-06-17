package com.eurya.luajit;

import android.app.Activity;
import android.os.Bundle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.content.Intent;

public class Welcome extends Activity {

  private LuaJitApplication app;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = (LuaJitApplication) getApplication();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(
        () -> {
          try {
            unApk("luaLibs", app.luaLibs);
          } catch (IOException e) {
            e.printStackTrace();
          }
          ;
          handler.post(
              () -> {
                startActivity(new Intent(Welcome.this, LuaActivity.class));
              });
        });
    executor.shutdown();
  }

  private void unApk(String dir, String extDir) throws IOException {
    int i = dir.length() + 1;
    ZipFile zip = new ZipFile(getApplicationInfo().publicSourceDir);
    Enumeration<? extends ZipEntry> entries = zip.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String name = entry.getName();
      if (name.indexOf(dir) != 0) continue;
      String path = name.substring(i);
      if (entry.isDirectory()) {
        File f = new File(extDir + File.separator + path);
        if (!f.exists()) {
          //noinspection ResultOfMethodCallIgnored
          f.mkdirs();
        }
      } else {
        String fname = extDir + File.separator + path;
        File ff = new File(fname);
        File temp = new File(fname).getParentFile();
        if (!temp.exists()) {
          if (!temp.mkdirs()) {
            throw new RuntimeException("create file " + temp.getName() + " fail");
          }
        }
        try {
          if (ff.exists() && entry.getSize() == ff.length()) continue;
        } catch (NullPointerException ignored) {
        }
        FileOutputStream out = new FileOutputStream(extDir + File.separator + path);
        InputStream in = zip.getInputStream(entry);
        byte[] buf = new byte[4096];
        int count = 0;
        while ((count = in.read(buf)) != -1) {
          out.write(buf, 0, count);
        }
        out.close();
        in.close();
      }
    }
    zip.close();
  }
}
