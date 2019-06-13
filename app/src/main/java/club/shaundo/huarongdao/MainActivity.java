package club.shaundo.huarongdao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AbsoluteLayout layout = findViewById(R.id.absoluteLayout);
        setContentView(layout);
        ArrayList<Chess> chesses = new ArrayList<>();
        Intent intent = getIntent();
        int MapFileId = intent.getIntExtra("mapfile", -1);
        HRDFileReader hrdFileReader = new HRDFileReader(getResources().openRawResource(MapFileId));
        for (int i = 0; i < hrdFileReader.getAllCount(); ++i) {
            Chess chess = hrdFileReader.getChess(this);
            layout.addView(chess);
            chesses.add(chess);
        }
        for (int i = 0; i < hrdFileReader.getAllCount(); ++i) {
            chesses.get(i).setRelativeChesses(chesses);
            chesses.get(i).setBounds(0, 0, hrdFileReader.getMaxWidth(), hrdFileReader.getMaxHeight());
        }
        for (int i = 0; i < hrdFileReader.getAllCount(); ++i){
            if (chesses.get(i).getText().equals(hrdFileReader.getWinnerName())){
                chesses.get(i).setWinner(hrdFileReader.getWinnerX(), hrdFileReader.getWinnerY());
            }
        }
    }
}

class Pair{
    int x;
    int y;
    Pair(int x, int y){this.x = x; this.y = y;}
}

class HRDFileReader {
    private Scanner reader;
    private int m_allCount;
    private int max_width, max_height;
    private String winner_name;
    private int winner_x, winner_y;

    public HRDFileReader(InputStream istream) {
        reader = new Scanner(istream);
        m_allCount = reader.nextInt();
        max_width = reader.nextInt();
        max_height = reader.nextInt();
        winner_name = reader.next();
        winner_x = reader.nextInt();
        winner_y = reader.nextInt();
    }

    public int getAllCount() { return m_allCount; }

    public int getMaxWidth() { return max_width; }

    public int getMaxHeight() { return max_height; }

    public Chess getChess(Context context) {
        Chess chess = new Chess(context, reader.next(), reader.nextInt(), reader.nextInt(), reader.nextInt(), reader.nextInt());
        return chess;
    }

    public String getWinnerName(){ return winner_name; }

    public int getWinnerX() { return winner_x; }

    public int getWinnerY() { return winner_y; }
}

class Chess extends MyButton {
    private int WIDTH = getResources().getDisplayMetrics().widthPixels / 4;
    private int leftX, leftY;
    private int rightX, rightY;
    private ArrayList<Chess> relativeChesses;

    private int nowX, nowY;
    private int minX, minY, maxX, maxY;
    private boolean isWinner = false;
    private int winner_x, winner_y;

    Chess(final Context context, String name, final int leftx, int lefty, int rightx, int righty) {
        super(context);

        this.leftX = leftx * WIDTH;
        this.leftY = lefty * WIDTH;
        this.rightX = rightx * WIDTH;
        this.rightY = righty * WIDTH;

        this.setWidth(rightX - leftX);
        this.setHeight(rightY - leftY);
        this.setX(leftX);
        this.setY(leftY);
        this.setText(name);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                        nowX = (int) event.getX();
                        nowY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                        int dx = (int) event.getX() - nowX;
                        int dy = (int) event.getY() - nowY;
//                        nowX = (int) event.getRawX();
//                        nowY = (int) event.getRawY();
                        if (leftX + dx < minX){
                            dx = minX - leftX;
                        }
                        else if (rightX + dx > maxX){
                            dx = maxX - rightX;
                        }
                        if (leftY + dy < minY){
                            dy = minY - leftY;
                        }
                        else if (rightY + dy > maxY){
                            dy = maxY - rightY;
                        }
                        Pair move = new Pair(dx, dy);
                        for (int i = 0; i < relativeChesses.size(); ++i) {
                            if (relativeChesses.get(i) != Chess.this) {
                                move = getTrueMove(Chess.this, relativeChesses.get(i), move);
                                //System.out.println("MOVE!::" + move.x + " " + move.y);
                            }
                        }
                        leftX += move.x;
                        leftY += move.y;
                        rightX += move.x;
                        rightY += move.y;
//                        System.out.println("FFFFFFFFFF!dx dy " + dx + " " + dy);
//                        System.out.println("FFFFFFFFFF!movex movey " + move.x + " " + move.y);
                        setX(leftX);
                        setY(leftY);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: // 手指松开对应的事件
                        leftX = Math.round((float) leftX / WIDTH) * WIDTH;
                        leftY = Math.round((float) leftY / WIDTH) * WIDTH;
                        rightX = Math.round((float) rightX / WIDTH) * WIDTH;
                        rightY = Math.round((float) rightY / WIDTH) * WIDTH;
                        setX(leftX);
                        setY(leftY);
                        if (isWinner && winner_x * WIDTH == leftX && winner_y * WIDTH == leftY) {
                            Toast.makeText(context, "你成功了!", Toast.LENGTH_LONG).show();
                            new Thread() {
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    ((Activity) context).finish();
                                }
                            }.start();
                            return true;
                        }
                        break;
                }
                return true;// 不会中断触摸事件的返回
            }
        });
    }

    public void setRelativeChesses(ArrayList<Chess> chesses) {
        relativeChesses = chesses;
    }

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        this.minX = minX * WIDTH;
        this.minY = minY * WIDTH;
        this.maxX = maxX * WIDTH;
        this.maxY = maxY * WIDTH;
    }

    public void setWinner(int X, int Y){
        this.isWinner = true;
        this.winner_x = X;
        this.winner_y = Y;
    }

    private Pair getTrueMove(Chess chess1, Chess chess2, Pair rawMove){
        if (chess1.rightY <= chess2.leftY && chess1.rightY + rawMove.y > chess2.leftY){
            if (chess1.leftX < chess2.rightX && chess1.rightX > chess2.leftX){
                //如果chess1在chess2的上方，并且有可能向下走重叠
                rawMove.y = chess2.leftY - chess1.rightY;
            }
            else if (chess1.leftX == chess2.rightX || chess1.rightX == chess2.leftX){
                //特判：chess1正好在chess2的右上方/左上方
                //相当于可以随机认为用户可以向下或者向左（向右）移动，但是不能同时向两个方向移动
                if (Math.abs(rawMove.x) > Math.abs(rawMove.y)){
                    rawMove.y = chess2.leftY - chess1.rightY;
                }
                else {
                    rawMove.x = 0;
                }
            }
        }
        else if (chess2.rightY <= chess1.leftY && chess1.leftY + rawMove.y < chess2.rightY){
            if (chess1.leftX < chess2.rightX && chess1.rightX > chess2.leftX){
                //如果chess1在chess2的下方，并且有可能向上走重叠
                rawMove.y = chess2.rightY - chess1.leftY;
            }
            else if (chess1.leftX == chess2.rightX || chess1.rightX == chess2.leftX){
                //特判：chess1正好在chess2的右下方/左下方
                //相当于可以随机认为用户可以向上或者向左（向右）移动，但是不能同时向两个方向移动
                if (Math.abs(rawMove.x) > Math.abs(rawMove.y)){
                    rawMove.y = chess2.rightY - chess1.leftY;
                }
                else {
                    rawMove.x = 0;
                }
            }
        }

        if (chess1.rightX <= chess2.leftX && chess1.rightX + rawMove.x > chess2.leftX){
            if (chess1.rightY > chess2.leftY && chess1.leftY < chess2.rightY){
                //如果chess1在chess2的左方，并且有可能向右走重叠
                rawMove.x = chess2.leftX - chess1.rightX;
            }
            else if (chess1.rightY == chess2.leftY || chess1.leftY == chess2.rightY){
                //特判：chess1正好在chess2的左上方/左下方
                //相当于可以随机认为用户可以向右或者向上（向下）移动，但是不能同时向两个方向移动
                if (Math.abs(rawMove.x) > Math.abs(rawMove.y)){
                    rawMove.y = 0;
                }
                else {
                    rawMove.x = chess2.leftX - chess1.rightX;
                }
            }
        }
        else if (chess2.rightX <= chess1.leftX && chess1.leftX + rawMove.x < chess2.rightX){
            if (chess1.rightY > chess2.leftY && chess1.leftY < chess2.rightY){
                //如果chess1在chess2的右方，并且有可能向左走重叠
                rawMove.x = chess2.rightX - chess1.leftX;
            }
            else if (chess1.rightY == chess2.leftY || chess1.leftY == chess2.rightY){
                //特判：chess1正好在chess2的右上方/右下方
                //相当于可以随机认为用户可以向左或者向上（向下）移动，但是不能同时向两个方向移动
                if (Math.abs(rawMove.x) > Math.abs(rawMove.y)){
                    rawMove.y = 0;
                }
                else {
                    rawMove.x = chess2.rightX - chess1.leftX;
                }
            }
        }
        return rawMove;
    }
}