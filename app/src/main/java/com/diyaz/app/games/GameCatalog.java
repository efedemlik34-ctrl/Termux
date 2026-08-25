package com.diyaz.app.games;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Original, free-to-play DİYAZ mini-game catalog. */
public final class GameCatalog {
    private GameCatalog() {}

    public static final class Game {
        public final String id;
        public final String title;
        public final String description;
        public Game(String id, String title, String description) {
            this.id = id; this.title = title; this.description = description;
        }
    }

    public static List<Game> all() {
        return Collections.unmodifiableList(Arrays.asList(
            new Game("ludo", "Ludo", "Arkadaşlarınla klasik zar tabanlı masa oyunu."),
            new Game("bingo", "Bingo", "Numaraları eşleştir ve çizgiyi tamamla."),
            new Game("eightball", "8 Ball", "Yerel ağ odasında sıra tabanlı bilardo."),
            new Game("snakes", "Yılanlar ve Merdivenler", "Basit ve eğlenceli masa oyunu."),
            new Game("quiz", "Mini Bilgi", "Arkadaşlarına karşı kısa bilgi soruları."),
            new Game("drawguess", "Çiz ve Tahmin Et", "Bir oyuncu çizer, diğerleri tahmin eder."),
            new Game("wordchain", "Kelime Zinciri", "Son harften yeni kelime üret."),
            new Game("reaction", "Hızlı Tepki", "Ekran değiştiğinde en hızlı dokunuşu yap."),
            new Game("memory", "Hafıza", "Kart çiftlerini bul ve puan topla.")
        ));
    }
}
