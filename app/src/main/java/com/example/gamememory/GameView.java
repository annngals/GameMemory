package com.example.gamememory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

class Card {
    Paint paint;

    boolean isOpen = false; // перевёрнута ли карта
    Bitmap btm_Open, btm_NoOpen;
    int width, height, x, y;

    public Card(Bitmap btm_Open,Bitmap btm_NoOpen, int x, int y, int width, int height) {
        this.btm_Open = btm_Open;
        this.btm_NoOpen = btm_NoOpen;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void draw(Canvas canvas) {
        @SuppressLint("DrawAllocation") Bitmap bmHalf = null;
        if (isOpen) {
            bmHalf = Bitmap.createScaledBitmap(btm_Open, width, height, false);
        }
        else {
            bmHalf = Bitmap.createScaledBitmap(btm_NoOpen, width, height, false);
        }
        canvas.drawBitmap(bmHalf, x, y, paint);
    }

    public boolean flip (int touch_x, int touch_y) {
        if (touch_x == x &&  touch_y == y ) {
            isOpen = ! isOpen;
            return true;
        } else return false;
    }
}


public class GameView extends View {
    // пауза для запоминания карт
    final int PAUSE_LENGTH = 2; // в секундах
    boolean isOnPauseNow = false;
    Context context;

    ArrayList<Card> cards = new ArrayList<>(); // текущие карты на поле
    ArrayList<Bitmap> store = new ArrayList<>(); // для хранения всех карт

    Bitmap bitmapSource; // обратная сторона карты
    Paint paint;

    int n; // размер поля для игры
    int openedCard = 0;// число открытых карт
    int width, height; // ширина и высота канвы

    public GameView(Context context, int size, int width, int height) {

        super(context);
        this.context = context;
        this.width = width;
        this.height = height;
        this.n = size;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.image_part_055);
        for (int i = 1; i < 55; i++) {
            String url;
            if (i<10) {
                url = getContext().getString(R.string.img_path00)+ String.valueOf(i);
            } else {
                url = getContext().getString(R.string.img_path0)+ String.valueOf(i);
            }
            int imageResource = getResources().getIdentifier(url, null, context.getPackageName());
            store.add(BitmapFactory.decodeResource(getResources(),imageResource));
        }
        addCards();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Card c: cards) {
            c.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        x = x / (width / n);
        y = y / (height / n);
        int dx = width / n, dy = height / n;
        x = x * dx;
        y = y * dy;
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isOnPauseNow)
        {
            for (Card c: cards){
                if (openedCard == 0){
                    if (c.flip(x,y)){
                        openedCard++;
                        invalidate();
                        return true;
                    }
                }
                if (openedCard == 1) {
                    // перевернуть карту с задержкой
                    if (!c.isOpen && c.flip(x, y)) {
                        openedCard++;
                        invalidate();
                        PauseTask task = new PauseTask();
                        task.execute(PAUSE_LENGTH);
                        isOnPauseNow = true;
                        return true;
                    }
                }
            }
        }

        return true;
    }

    public void checkOpenCardsEqual(){
        Card card1 = null; // TODO: переименовать переменные
        Card card2 = null;
        for (int i = 0; i < cards.size(); i++){
            if (card1 == null && cards.get(i).isOpen){
                card1 = cards.get(i);
            }
            else if (card2 == null && cards.get(i).isOpen){
                card2 = cards.get(i);
            }
        }
        if (equals(card1.btm_Open,card2.btm_Open)){
            cards.remove(card1);
            cards.remove(card2);
        }
    }

    class PauseTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            Log.d("mytag", "Pause started");
            try {
                Thread.sleep(integers[0] * 1000); // передаём число секунд ожидания
            } catch (InterruptedException e) {}
            Log.d("mytag", "Pause finished");
            return null;
        }

        // после паузы, перевернуть все карты обратно


        @Override
        protected void onPostExecute(Void aVoid) {
            checkOpenCardsEqual();
            if (cards.size() == 0){
                AlertDialog show = new AlertDialog.Builder(context)
                        .setTitle(R.string.end)
                        .setMessage(R.string.newgame)
                        .setCancelable(false)
                        .setPositiveButton(R.string.playmore, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newGame();
                            }
                        }).setNegativeButton(R.string.mainmenu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            }
                        }).show();
            }
            for (Card c: cards) {
                if (c.isOpen) {
                    c.isOpen = false;
                }
            }
            openedCard = 0;
            isOnPauseNow = false;
            invalidate();
        }
    }

    public void newGame() {
        addCards();
        invalidate();
    }

    public void addCards() {
        Random r = new Random();
        int dx = width / n, dy = height / n;
        int tek = 0;
        int temp = 0;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                int k = r.nextInt(store.size());
                if (temp == 0){
                    tek = k;
                    cards.add(new Card(store.get(k), bitmapSource, 0, 0, dx, dy));

                }
                else {
                    cards.add(new Card(store.get(tek), bitmapSource, 0, 0, dx, dy));
                    temp = -1;
                }
                temp++;
            }
        }
        Collections.shuffle(cards);
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                int k = r.nextInt(store.size());
                cards.get(i * n + j).x = dx * i;
                cards.get(i * n + j).y = dy * j;
            }
        }
    }

    public boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

}

