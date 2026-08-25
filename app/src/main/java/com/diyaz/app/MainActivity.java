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
import com.diyaz.app.games.PartyGames;
import com.diyaz.app.lan.LanDiscovery;
import com.diyaz.app.lan.LanVoiceRoom;

public class MainActivity extends Activity {
    private LinearLayout content;
    private LanDiscovery discovery;
    private LanVoiceRoom voiceRoom;

    @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); showHome(); }
    private TextView title(String text){ TextView v=new TextView(this); v.setText(text); v.setTextSize(24); v.setGravity(Gravity.CENTER_VERTICAL); v.setPadding(24,32,24,20); return v; }
    private Button button(String text, View.OnClickListener listener){ Button b=new Button(this); b.setText(text); b.setAllCaps(false); b.setOnClickListener(listener); return b; }
    private void base(String heading){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.addView(title(heading));
        ScrollView scroll=new ScrollView(this); content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(20,0,20,20); scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this); nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.addView(button("Ana Sayfa",v->showHome()),new LinearLayout.LayoutParams(0,-2,1)); nav.addView(button("Odalar",v->showRooms()),new LinearLayout.LayoutParams(0,-2,1)); nav.addView(button("Oyunlar",v->showGames()),new LinearLayout.LayoutParams(0,-2,1)); nav.addView(button("Profil",v->showProfile()),new LinearLayout.LayoutParams(0,-2,1)); root.addView(nav); setContentView(root);
    }
    private void showHome(){ base("DİYAZ"); content.addView(title("Sesli sohbet + arkadaşlarla oyun")); content.addView(button("LAN odalarını bul",v->showRooms())); content.addView(button("Yeni LAN odası oluştur",v->createRoom())); content.addView(button("DİYAZ Oyunları",v->showGames())); TextView i=new TextView(this); i.setText("Aynı Wi-Fi üzerindeki arkadaşlarınla oda oluştur, konuş ve mini oyunları birlikte oyna."); i.setPadding(8,25,8,20); content.addView(i); }
    private void showRooms(){ base("LAN Sesli Odalar"); content.addView(button("Yakındaki odaları tara",v->scanRooms())); content.addView(button("Bu cihazda oda oluştur",v->createRoom())); }
    private void scanRooms(){ content.addView(title("Aranıyor...")); discovery=new LanDiscovery(this); discovery.setListener((name,host,port)->runOnUiThread(()->{ content.addView(button(name+" • "+host+":"+port,v->joinRoom(host))); })); discovery.start(); Toast.makeText(this,"Aynı Wi-Fi'daki DİYAZ odaları aranıyor",Toast.LENGTH_SHORT).show(); }
    private void createRoom(){ base("Oda Oluştur"); content.addView(button("LAN odasını başlat",v->hostRoom())); content.addView(button("Oyunlu oda başlat",v->hostRoom())); }
    private void hostRoom(){ try { voiceRoom=new LanVoiceRoom(); int port=voiceRoom.host(); base("DİYAZ LAN Odası"); content.addView(title("Oda hazır")); content.addView(text("Bu telefonu oda sahibi olarak kullan. Aynı Wi-Fi'daki arkadaşların odayı tarayabilir.")); content.addView(text("Ses portu: "+port)); content.addView(button("Mikrofon izni",v->requestMic())); content.addView(button("Oyunları aç",v->showGames())); content.addView(button("Odayı kapat",v->{voiceRoom.stop();showRooms();})); } catch(Exception e){ Toast.makeText(this,"Oda başlatılamadı: "+e.getMessage(),Toast.LENGTH_LONG).show(); } }
    private void joinRoom(String host){ try { voiceRoom=new LanVoiceRoom(); voiceRoom.connect(host); base("DİYAZ LAN Odası"); content.addView(text("Bağlandı: "+host)); content.addView(button("Mikrofonu aç",v->requestMic())); content.addView(button("Oyunları aç",v->showGames())); content.addView(button("Ayrıl",v->{voiceRoom.stop();showRooms();})); } catch(Exception e){ Toast.makeText(this,"Bağlantı kurulamadı: "+e.getMessage(),Toast.LENGTH_LONG).show(); } }
    private void showGames(){ base("Oyunlar"); new PartyGames(this).show(content); }
    private void showMessages(){base("Mesajlar"); content.addView(text("Yerel sürümde mesajlaşma için oda içi LAN bağlantısı kullanılacak."));}
    private void showProfile(){base("Profil"); content.addView(text("DİYAZ kullanıcısı\n\nSeviye: 1\nRozetler: 0\nArkadaşlar: yerel cihazlar"));}
    private TextView text(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(18);t.setPadding(10,20,10,20);return t;}
    private void requestMic(){ if(Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1001);return;} Toast.makeText(this,"Mikrofon hazır",Toast.LENGTH_SHORT).show(); }
    @Override protected void onDestroy(){ if(discovery!=null) discovery.stop(); if(voiceRoom!=null) voiceRoom.stop(); super.onDestroy(); }
}
