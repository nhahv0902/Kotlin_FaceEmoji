package com.nhahv.faceemoji.data.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;

import pl.droidsonroids.gif.GifDrawable;

public class Emojis {

    static {
        String[][] r0 = new String[3][];
        r0[0] = new String[]{"neymoji_body_300", "neymoji_body_301", "neymoji_body_302", "neymoji_body_303", "neymoji_body_304", "neymoji_body_305", "neymoji_body_306", "neymoji_body_307", "neymoji_body_308", "neymoji_face_001", "neymoji_face_002", "neymoji_face_003", "neymoji_face_004", "neymoji_face_005", "neymoji_face_006", "neymoji_face_007", "neymoji_face_008", "neymoji_face_009", "neymoji_face_010", "neymoji_face_011", "neymoji_face_012", "neymoji_face_013", "neymoji_face_014", "neymoji_face_015", "neymoji_face_016", "neymoji_face_017", "neymoji_face_018", "neymoji_face_019", "neymoji_face_020", "neymoji_face_021", "neymoji_face_022", "neymoji_face_023", "neymoji_face_024", "neymoji_face_025", "neymoji_face_026", "neymoji_face_027", "neymoji_face_028", "neymoji_face_029", "neymoji_face_030", "neymoji_face_031", "neymoji_face_032", "neymoji_face_033"};
        r0[1] = new String[]{"neymoji_stuff_001", "neymoji_stuff_002", "neymoji_stuff_003", "neymoji_stuff_004", "neymoji_stuff_005", "neymoji_stuff_006", "neymoji_stuff_007", "neymoji_stuff_008", "neymoji_stuff_009", "neymoji_stuff_010", "neymoji_stuff_011", "neymoji_stuff_012", "neymoji_stuff_013", "neymoji_stuff_014", "neymoji_stuff_015", "neymoji_stuff_016", "neymoji_stuff_017", "neymoji_stuff_018", "neymoji_stuff_019", "neymoji_stuff_020", "neymoji_stuff_021", "neymoji_stuff_023", "neymoji_stuff_024", "neymoji_stuff_025", "neymoji_stuff_026", "neymoji_stuff_101", "neymoji_stuff_102", "neymoji_stuff_103", "neymoji_stuff_104", "neymoji_stuff_105", "neymoji_stuff_106", "neymoji_stuff_107", "neymoji_stuff_108", "neymoji_stuff_109", "neymoji_stuff_110", "neymoji_stuff_111", "neymoji_stuff_112", "neymoji_stuff_113", "neymoji_stuff_114", "neymoji_stuff_115", "neymoji_stuff_116", "neymoji_stuff_201", "neymoji_stuff_202", "neymoji_stuff_203", "neymoji_stuff_204", "neymoji_stuff_205", "neymoji_object_301", "neymoji_object_302", "neymoji_object_303", "neymoji_object_304", "neymoji_object_305", "neymoji_object_306", "neymoji_object_307", "neymoji_object_308"};
        r0[2] = new String[]{"neymoji_phrases_101", "neymoji_phrases_102", "neymoji_phrases_103", "neymoji_phrases_104", "neymoji_phrases_105", "neymoji_phrases_106", "neymoji_phrases_107", "neymoji_phrases_108", "neymoji_phrases_109", "neymoji_phrases_110", "neymoji_phrases_111", "neymoji_phrases_112", "neymoji_phrases_113", "neymoji_phrases_114", "neymoji_phrases_115", "neymoji_phrases_116", "neymoji_phrases_117", "neymoji_phrases_118", "neymoji_phrases_119", "neymoji_phrases_120", "neymoji_phrases_121", "neymoji_phrases_122", "neymoji_phrases_123", "neymoji_phrases_124", "neymoji_phrases_125", "neymoji_phrases_126", "neymoji_phrases_127", "neymoji_phrases_128", "neymoji_phrases_129", "neymoji_phrases_130", "neymoji_phrases_131", "neymoji_phrases_132", "neymoji_phrases_133", "neymoji_phrases_134", "neymoji_phrases_135", "neymoji_phrases_136", "neymoji_phrases_137", "neymoji_phrases_138", "neymoji_phrases_139", "neymoji_phrases_140", "neymoji_phrases_141", "neymoji_phrases_142", "neymoji_phrases_143", "neymoji_phrases_144", "neymoji_phrases_145", "neymoji_phrases_146", "neymoji_phrases_147", "neymoji_phrases_001", "neymoji_phrases_002", "neymoji_phrases_003", "neymoji_phrases_004", "neymoji_phrases_005", "neymoji_phrases_006", "neymoji_phrases_007", "neymoji_phrases_008", "neymoji_phrases_009", "neymoji_phrases_010", "neymoji_phrases_011", "neymoji_phrases_012", "neymoji_phrases_013", "neymoji_phrases_014", "neymoji_phrases_015", "neymoji_phrases_016", "neymoji_phrases_017", "neymoji_phrases_018", "neymoji_phrases_019", "neymoji_phrases_020", "neymoji_phrases_021", "neymoji_phrases_022", "neymoji_phrases_023", "neymoji_phrases_024", "neymoji_phrases_025", "neymoji_phrases_026", "neymoji_phrases_027", "neymoji_phrases_028", "neymoji_phrases_029", "neymoji_phrases_030", "neymoji_phrases_031", "neymoji_phrases_032", "neymoji_phrases_033", "neymoji_phrases_034", "neymoji_phrases_035", "neymoji_phrases_036", "neymoji_phrases_037", "neymoji_phrases_038", "neymoji_phrases_039", "neymoji_phrases_040", "neymoji_phrases_041", "neymoji_phrases_042", "neymoji_phrases_043", "neymoji_phrases_044", "neymoji_phrases_045", "neymoji_phrases_046", "neymoji_phrases_047"};
        emojis = r0;
        r0 = new String[4][];
        r0[0] = new String[]{"neymar_heads_xrare_001", "neymar_heads_xrare_002", "neymar_heads_xrare_003", "neymar_heads_xrare_004", "neymar_heads_xrare_005", "neymar_heads_xrare_006"};
        r0[1] = new String[]{"neymar_heads_rare_001", "neymar_heads_rare_002", "neymar_heads_rare_003", "neymar_heads_rare_004", "neymar_heads_rare_005", "neymar_heads_rare_006"};
        r0[2] = new String[]{"neymar_heads_uncmn_001", "neymar_heads_uncmn_002", "neymar_heads_uncmn_003", "neymar_heads_uncmn_004", "neymar_heads_uncmn_005", "neymar_heads_uncmn_006"};
        r0[3] = new String[]{"neymar_heads_cmn_001", "neymar_heads_cmn_002", "neymar_heads_cmn_004", "neymar_heads_cmn_005", "neymar_heads_cmn_006", "neymar_heads_cmn_007", "neymar_heads_cmn_008", "neymar_heads_cmn_009", "neymar_heads_cmn_010", "neymar_heads_cmn_011", "neymar_heads_cmn_012", "neymar_heads_cmn_015", "neymar_heads_cmn_016"};
        emojis_collectible = r0;
    }

    public static final String[] GIFS = new String[0];
    public static final long NEXT_OPEN_INTERVAL = 86400000;
    public static int STUFF_INDEX = 1;
    public static final String[][] emojis;
    public static final String[] NIKE_STUFF = new String[]{emojis[STUFF_INDEX][0], emojis[STUFF_INDEX][1], emojis[STUFF_INDEX][2], emojis[STUFF_INDEX][3], emojis[STUFF_INDEX][7], emojis[STUFF_INDEX][8], emojis[STUFF_INDEX][9], emojis[STUFF_INDEX][10], emojis[STUFF_INDEX][11], emojis[STUFF_INDEX][15]};
    public static final String[] WATCH_STUFF = new String[]{emojis[STUFF_INDEX][12], emojis[STUFF_INDEX][13], emojis[STUFF_INDEX][14]};
    public static final String[][] emojis_collectible;
    public static final String[] emojis_collectible_section_names = new String[]{"Extra Rare", "Rare", "Uncommon", "Common"};
    public static final String[] gifAsVideoApps = new String[]{"com.whatsapp"};
    public static final String[] noWhiteBackgroundApps = new String[]{"com.google.android.talk", "com.google.android.gm"};
    public static final String[] squareMessageApps = new String[]{"com.whatsapp", "com.viber.voip", "com.skype.android"};
    public static final String[] whiteBackroundApps = new String[]{"com.whatsapp", "com.viber.voip", "com.snapchat.android", "com.twitter.android", "com.instagram.android"};


    public static boolean addWhiteBackground(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        for (String s : whiteBackroundApps) {
            if (s.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addWhiteBackgroundToMessage(String packageName) {
        if (!(packageName == null || packageName.isEmpty())) {
            for (String s : noWhiteBackgroundApps) {
                if (s.equals(packageName)) {
                    return false;
                }
            }
            if (packageName.contains("mail")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSquareMessage(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        for (String s : squareMessageApps) {
            if (s.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean convertGifToVideo(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }
        for (String s : gifAsVideoApps) {
            if (s.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static int sections() {
        return emojis.length;
    }

    public static int sectionsCollectible() {
        return emojis_collectible.length;
    }

    public static int sectionSize(int section) {
        return emojis[section].length;
    }

    public static int sectionSizeCollectible(int section) {
        return emojis_collectible[section].length;
    }

    public static String collectibleSectionName(int section) {
        return emojis_collectible_section_names[section];
    }

    public static String collectibleName(int section, int index) {
        return "";
    }

    public static String getSmallImageName(int section, int index) {
        return emojis[section][index] + "_80.png";
    }

    public static String getMediumImageName(int section, int index) {
        return emojis[section][index] + "_120.png";
    }

    public static String getLargeImageName(int section, int index) {
        String name = emojis[section][index];
        if (Arrays.asList(GIFS).indexOf(name) == -1) {
            return name + "_512.png";
        }
        return name + ".gif";
    }

    public static String getCollectibleSmallImageName(int section, int index) {
        return emojis_collectible[section][index] + "_80.png";
    }

    public static String getCollectibleMediumImageName(int section, int index) {
        return emojis_collectible[section][index] + "_120.png";
    }

    public static String getCollectibleLargeImageName(int section, int index) {
        String name = emojis_collectible[section][index];
        if (Arrays.asList(GIFS).indexOf(name) == -1) {
            return name + "_512.png";
        }
        return name + ".gif";
    }

    public static boolean isNike(int section, int index) {
        return Arrays.asList(NIKE_STUFF).indexOf(emojis[section][index]) != -1;
    }

    public static boolean isWatch(int section, int index) {
        return Arrays.asList(WATCH_STUFF).indexOf(emojis[section][index]) != -1;
    }

    public static boolean isMediumImageName(String name) {
        int section;
        for (section = 0; section < sectionsCollectible(); section++) {
            int i;
            for (i = 0; i < sectionSizeCollectible(section); i++) {
                if (name.equals(getCollectibleMediumImageName(section, i))) {
                    return true;
                }
            }
        }
        for (section = 0; section < sections(); section++) {
            for (int i = 0; i < sectionSize(section); i++) {
                if (name.equals(getMediumImageName(section, i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Drawable getImageDrawable(Context context, String name) {
        try {
            AssetManager assetManager = context.getAssets();
            if (!name.endsWith("png")) {
                return new GifDrawable(assetManager.openFd(name));
            }
            InputStream is = assetManager.open(name);
            new Options().inDensity = 240;
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getImageUri(Context context, String name, boolean gif, int percentage, String packageName) {
        Uri uri = null;
        boolean convertGifToMp4 = false;
        if (gif) {
            convertGifToMp4 = convertGifToVideo(packageName);
        }
        try {
            InputStream is;
            AssetManager assetManager = context.getAssets();
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_PICTURES + "/Neymoji";
            File dir = new File(extStorageDirectory);
            if (dir.exists()) {
                String[] children = dir.list();
                for (String file : children) {
                    new File(dir, file).delete();
                }
            } else {
                dir.mkdir();
            }
            Date date = new Date();
            String extension = "png";
            if (gif) {
                if (convertGifToMp4) {
                    extension = "mp4";
                } else {
                    extension = "gif";
                }
            }
            File file2 = new File(extStorageDirectory, (date.getTime() / 1000) + "_neymoji." + extension);
            if (!file2.exists()) {
                file2.createNewFile();
            }
            if (convertGifToMp4 && gif) {
                is = context.getResources().openRawResource(context.getResources().getIdentifier(name.substring(0, name.length() - 4), "raw", context.getPackageName()));
            } else {
                is = assetManager.open(name);
            }
            OutputStream fileOutputStream = new FileOutputStream(file2);
            if (gif) {
                byte[] buffer = new byte[1024];
                while (true) {
                    int bytesRead = is.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            } else {
                Bitmap bm = BitmapFactory.decodeStream(is);
                int orgSize = bm.getWidth();
                int size = (int) (((double) orgSize) * (((double) (percentage + 30)) / 100.0d));
                int padding = (orgSize - size) / 2;
                bm = Bitmap.createScaledBitmap(bm, size, size, true);
                Bitmap sendBitmap = Bitmap.createBitmap(orgSize, orgSize, bm.getConfig());
                Canvas canvas = new Canvas(sendBitmap);
                if (addWhiteBackground(packageName)) {
                    canvas.drawARGB(255, 255, 255, 255);
                }
                canvas.drawBitmap(bm, (float) padding, (float) padding, null);
                sendBitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            uri = Uri.fromFile(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static Uri getImageUri(Bitmap bitmap, String packageName) {
        try {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_PICTURES + "/Neymoji";
            File dir = new File(extStorageDirectory);
            if (dir.exists()) {
                String[] children = dir.list();
                for (String file : children) {
                    new File(dir, file).delete();
                }
            } else {
                dir.mkdir();
            }
            File file2 = new File(extStorageDirectory, (new Date().getTime() / 1000) + "_neymoji." + "png");
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file2);
            if (addWhiteBackgroundToMessage(packageName)) {
                int height = (bitmap.getWidth() * 2) / 3;
                if (isSquareMessage(packageName)) {
                    height = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
                } else if (height <= bitmap.getHeight()) {
                    height = bitmap.getHeight();
                }
                Bitmap sendBitmap = Bitmap.createBitmap(bitmap.getWidth(), height, bitmap.getConfig());
                Canvas canvas = new Canvas(sendBitmap);
                canvas.drawARGB(255, 255, 255, 255);
                int widthImage = bitmap.getWidth() - 30;
                int heightImage = (int) (((double) bitmap.getHeight()) * (((double) widthImage) / ((double) bitmap.getWidth())));
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, widthImage, heightImage, true), 15.0f, (float) (heightImage > height ? 0 : (height - heightImage) / 2), null);
                sendBitmap.compress(CompressFormat.PNG, 100, outStream);
            } else {
                bitmap.compress(CompressFormat.PNG, 100, outStream);
            }
            outStream.flush();
            outStream.close();
            return Uri.fromFile(file2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isImageGif(String name) {
        return name.endsWith("gif");
    }

    public static String getImageType(boolean gif) {
        return gif ? "image/gif" : "image/png";
    }
}
