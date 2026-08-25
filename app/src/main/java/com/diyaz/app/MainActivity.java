package com.diyaz.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private LinearLayout content;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }
    private TextView title(String text) { TextView v=new TextView(this); v.setText(text); v.setTextSize(24); v.setGravity(Gravity.CENTER_VERTICAL); v.setPadding(24,32,24,20); return v; }
    private Button button(String text, View.OnClickListener listener) { Button b=new Button(this); b.setText(text); b.setAllCaps(false); b.setOnClickListener(listener); return b; }
    private void base(String heading) {
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.addView(title(heading));
        ScrollView scroll=new ScrollView(this); content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(20,0,20,20); scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this); nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.addView(button("Ana Sayfa",v->showHome()),new LinearLayout.LayoutParams(0,-2,1)); nav.addView(button("Odalar",v->showRooms()),new LinearLayout.LayoutParams(0,-2,1)); nav.addView(button("Mesajlar",v->showMessages()),new LinearLayout.LayoutParams(0,-2,1)); nav.addView(button("Profil",v->showProfile()),new LinearLayout.LayoutParams(0,-2,1)); root.addView(nav); setContentView(root);
    }
    private void showHome(){ base("DİYAZ"); content.addView(title("Canlı sesli sohbet")); content.addView(button("Popüler odaları keşfet",v->showRooms())); content.addView(button("Yeni oda oluştur",v->createRoom())); TextView i=new TextView(this); i.setText("DİYAZ: sesli odalar, mesajlaşma ve topluluk uygulaması."); i.setPadding(8,30,8,20); content.addView(i); }
    private void showRooms(){ base("Sesli Odalar"); addRoom("Müzik ve Sohbet","12 kişi • 4 mikrofon"); addRoom("Günün Muhabbeti","7 kişi • 3 mikrofon"); addRoom("Oyun Sohbeti","18 kişi • 6 mikrofon"); content.addView(button("+ Oda oluştur",v->createRoom())); }
    private void addRoom(String name,String details){ TextView t=new TextView(this); t.setText(name+"\n"+details+"\nCANLI"); t.setTextSize(18); t.setPadding(20,20,20,20); content.addView(t); content.addView(button("Odaya katıl",v->enterRoom(name))); }
    private void createRoom(){ base("Oda Oluştur"); content.addView(button("DİYAZ Sohbet Odası oluştur",v->enterRoom("DİYAZ Sohbet Odası"))); }
    private void enterRoom(String roomName){ base(roomName); TextView state=new TextView(this); state.setText("Sesli oda\n\nDinleyici\n\nMikrofon koltukları: 4"); state.setTextSize(20); state.setPadding(10,20,10,30); content.addView(state); content.addView(button("Mikrofon izni ver",v->requestMic())); content.addView(button("Odadan çık",v->showRooms())); }
    private void requestMic(){ if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1001);return;} Toast.makeText(this,"Mikrofon izni hazır. Gerçek zamanlı ses için backend/signaling bağlantısı gerekir.",Toast.LENGTH_LONG).show(); }
    private void showMessages(){base("Mesajlar"); TextView t=new TextView(this); t.setText("Mesajlaşma altyapısı için backend bağlantısı gereklidir."); t.setTextSize(18); t.setPadding(10,20,10,20); content.addView(t);}
    private void showProfile(){base("Profil"); TextView t=new TextView(this); t.setText("DİYAZ kullanıcısı\n\nSeviye: 1\nRozetler: 0\nTakipçiler: 0"); t.setTextSize(18); t.setPadding(10,20,10,20); content.addView(t);}
}
