package com.tamoda.applauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ActivityNotFoundException;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;

@DesignerComponent(version = 2,
    description = "Launcher & Share Text ke Aplikasi (Tidak Nimpa Task)",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject(external = true)
public class TamodaLauncher extends AndroidNonvisibleComponent {

    private Context context;

    public TamodaLauncher(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    @SimpleFunction(description = "Buka aplikasi berdasarkan Package Name dengan Task terpisah.")
    public void BukaPindahAplikasi(String packageName) {
        PackageManager pm = context.getPackageManager();
        
        // Mengambil intent default dari package name
        Intent intent = pm.getLaunchIntentForPackage(packageName);

        if (intent != null) {
            // Memaksa pindah task/jendela baru
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            BerhasilDibuka();
        } else {
            AplikasiTidakDitemukan(packageName);
        }
    }

    @SimpleFunction(description = "Share teks langsung ke WhatsApp tanpa nimpa task.")
    public void ShareKeWhatsApp(String pesan) {
        // Package name resmi WhatsApp
        shareTextToApp("com.whatsapp", pesan);
    }

    @SimpleFunction(description = "Share teks langsung ke Telegram tanpa nimpa task.")
    public void ShareKeTelegram(String pesan) {
        // Package name resmi Telegram
        shareTextToApp("org.telegram.messenger", pesan);
    }
    
    @SimpleFunction(description = "Share teks/URL langsung ke Facebook tanpa nimpa task.")
    public void ShareKeFacebook(String pesan) {
        // Package name resmi Facebook
        shareTextToApp("com.facebook.katana", pesan);
    }

    // --- FUNGSI INTERNAL (Engine untuk Share Text) ---
    private void shareTextToApp(String packageName, String pesan) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage(packageName);
        
        // Menyisipkan teks yang ingin dikirim
        intent.putExtra(Intent.EXTRA_TEXT, pesan);
        
        // ---> INI KUNCI RAHASIANYA BOS <---
        // Memastikan jendela chat terbuka di luar aplikasi kita
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
            BerhasilDibuka();
        } catch (ActivityNotFoundException e) {
            // Akan tertrigger jika user belum menginstall WA/TG/FB
            AplikasiTidakDitemukan(packageName);
        }
    }

    @SimpleEvent(description = "Tertrigger jika aplikasi berhasil dipanggil atau pesan di-share.")
    public void BerhasilDibuka() {
        EventDispatcher.dispatchEvent(this, "BerhasilDibuka");
    }

    @SimpleEvent(description = "Tertrigger jika package name tidak terinstall di HP user.")
    public void AplikasiTidakDitemukan(String packageName) {
        EventDispatcher.dispatchEvent(this, "AplikasiTidakDitemukan", packageName);
    }
}
