package com.diyaz.app.games;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

/** Offline, non-monetary party mini-games for DİYAZ rooms. */
public class PartyGames {
    private final Activity activity;
    private final Random random = new Random();
    private LinearLayout box;
    public PartyGames(Activity activity) { this.activity = activity; }
    public void show(LinearLayout parent) {
        box = parent; parent.removeAllViews();
        TextView title = new TextView(activity); title.setText("DİYAZ Oyunları"); title.setTextSize(26); title.setTypeface(null, Typeface.BOLD); title.setGravity(Gravity.CENTER); title.setPadding(8,20,8,20); box.addView(title);
        addGame("⚡ Hızlı Tepki", v -> reaction()); addGame("🧠 Mini Bilgi", v -> trivia()); addGame("🔤 Kelime Zinciri", v -> wordChain());
        TextView note = new TextView(activity); note.setText("Oyunlar ücretsizdir ve gerçek para/bahis içermez."); note.setPadding(8,20,8,8); box.addView(note);
    }
    private void addGame(String name, View.OnClickListener click) { Button b=new Button(activity); b.setText(name); b.setAllCaps(false); b.setOnClickListener(click); box.addView(b); }
    private void reaction() {
        box.removeAllViews(); box.addView(label("Hazır ol... Buton açıldığında dokun!"));
        Button b=new Button(activity); b.setText("BEKLE"); b.setAllCaps(false); b.setEnabled(false); box.addView(b);
        b.postDelayed(() -> { b.setEnabled(true); b.setText("DOKUN!"); long start=System.currentTimeMillis(); b.setOnClickListener(v -> { long ms=System.currentTimeMillis()-start; Toast.makeText(activity,"Tepkin: "+ms+" ms",Toast.LENGTH_SHORT).show(); reaction(); }); },1200+random.nextInt(3000));
    }
    private void trivia() {
        box.removeAllViews(); String[][] q={{"Türkiye'nin başkenti hangisidir?","Ankara"},{"Bir haftada kaç gün vardır?","7"},{"Güneş bir gezegen midir?","Hayır"}}; String[] item=q[random.nextInt(q.length)]; box.addView(label(item[0]));
        Button a=new Button(activity); a.setText("Cevabı göster"); a.setAllCaps(false); a.setOnClickListener(v -> { a.setText(item[1]); a.setOnClickListener(x -> trivia()); }); box.addView(a);
    }
    private void wordChain() { box.removeAllViews(); box.addView(label("Kelime Zinciri\nBir kelime seç ve son harfiyle yeni kelime söyle.")); Button b=new Button(activity); b.setText("Yeni tur"); b.setAllCaps(false); b.setOnClickListener(v -> wordChain()); box.addView(b); }
    private TextView label(String text) { TextView t=new TextView(activity); t.setText(text); t.setTextSize(19); t.setPadding(10,20,10,20); return t; }
}
