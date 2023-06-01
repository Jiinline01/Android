package com.taewon.mygallag.sprites;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;
import java.util.Random;

public class AlienSprite extends Sprite{
    private Context context;
    private SpaceInvadersView game;
    ArrayList<AlienShotSprite> alienShotSprites;
    Handler fireHandler = null;
    boolean isDestroyed = false;
    public AlienSprite(Context context, SpaceInvadersView game, int resId, int x, int y){
        // 외계인 만들기
        super(context, resId, x, y);
        this.context = context;
        this.game = game;
        alienShotSprites = new ArrayList<>();
        Random r = new Random();
        int randomDx = r.nextInt(5);
        int randomDy = r.nextInt(5);
        // 0 ~ 4 중 랜덤한 숫자를 randomDx, randomDy 에 담음
        if(randomDy <= 0) dy = 1; // randomDy 가 0 보다 작거나 같을 경우 1을 dy 에 담음
        dx = randomDx; // dx = 0 ~ 4
        dy = randomDy; // dy = 1 ~ 4
        fireHandler = new Handler(Looper.getMainLooper()); // fireHandler 변수를 생성하고, Main Looper에 바인딩 된 핸들러를 할당합니다(Main Thread 의 MessageQueue 에 바인딩)
        // 메인 스레드의 Main Looper와 연결된 Handler 객체를 생성하는 것을 의미합니다.
        // 이를 통해 나중에 생성된 Runnable 객체를 Main Looper의 MessageQueue에 전달하고, UI 업데이트나 다른 메인 스레드에서 실행되어야 하는 작업을 예약할 수 있습니다.
        // MessageQueue 는 Looper 에 의해 보내지는 메세지들의 리스트를 모아놓고 있는 낮은 레벨의 클래스이다.
        // 메세지들은 직접적으로 메세지큐에 추가되는 것이 아닌, Looper와 관련된 Handler 객체를 통해 추가된다.
        fireHandler.postDelayed( // delay 사용하여 일정 시간 후에 실행될 작업을 예약
                new Runnable() { // 예약된 작업은 Runnable 객체로 정의
                    @Override
                    public void run() {
                        Log.d("run", "동작");
                        Random r = new Random();
                        boolean isFire = r.nextInt(100) + 1 <= 30;
                        // 0~99 중 랜덤 숫자 + 1 (1 ~ 100) 이 30보다 작거나 같을 경우 (30%의 확률)로 isFire 가 true 가 됨
                        if(isFire && !isDestroyed){ // isFire 가 true, isDestroy 가 false 인 경우 if 문 실행
                            fire(); // 총알을 발사
                            fireHandler.postDelayed(this, 1000); // 1 초마다 동일한 작업을 다시 반복
                        }
                    }
                }, 1000);
        // 위 코드는 1초마다 30%의 확률로 fire() 메서드를 호출하여 총알을 발사하고, 반복적으로 실행됩니다.
    }
    @Override
    public void move() {
        super.move();
        if ((dx < 0) && (x < 0) || ((dx > 0) && (x > 950))) { // 속도가 벽을 향하는 상태로 벽과 부딪힐 경우 벽과 반대 방향으로 이동하게 하는 if 문
            dx = -dx;
            if (y > game.screenH) { // y축이 game.screenH 보다 커질 경우 ( 화면 아래쪽으로 나갈 경우) 실행
                game.removeSprite(this); // 이 스프라이트를 제거함
            }
        }
    }

    @Override
    public void handleCollision(Sprite Other) { // Other 파라미터를 통해 다른 Sprite를 받음
        if (Other instanceof ShotSprite) { // Other Sprite가 ShotSprite 의 인스턴스인 경우 (가져온 Sprite 가 총알인 경우)
            game.removeSprite(Other); // 총알을 없애고
            game.removeSprite(this); // this(AlienSprite) 를 없앤다
            destroyAlien(); // destroyAlien() 을 호출해서 적 숫자 줄이고 아이템을 드랍하고... 등을 수행한다
            return;
        }
        if (Other instanceof SpecialshotSprite) { // Other Sprite가 SpecialshotSprite 의 인스턴스인 경우 (가져온 Sprite 가 필살기인 경우)
            game.removeSprite(this); // this(AlienSprite) 를 없앤다
            // 위처럼 game.removeSprite(Other); 를 없애지 않는 이유 : 필살기가 Alien을 죽였을 때 없어지지 않도록 하기 위함
            destroyAlien();
            return;
        }
    }

    private void destroyAlien() {
        isDestroyed = true; // isDestroyed 로 하여 죽은 Alien 이 총알을 발사하지 못하게 함
        game.setCurrEnemyCount(game.getCurrEnemyCount()-1); // 적의 숫자를 -1 하여 다시 세팅함
        for (int i = 0; i < alienShotSprites.size(); i++)
            game.removeSprite(alienShotSprites.get(i)); // alienShotSprites 에 남아있는 모든 sprites 를 removeSprite로 제거함
        spawnHealItem();
        spawnPowerItem();
        spawnSpeedItem();
        // 아이템 드랍
        game.setScore(game.getScore() + 1); // 게임 스코어로 +1 함
        MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // scoreTv 에 현재 게임 스코어를 가져와서 표시함
    }

    private void fire() {
        AlienShotSprite alienShotSprite = new AlienShotSprite(context, game, getX(), getY()+30, 16);
        // alienShotSprite 를 가져와서 x, y + 30 의 위치에 생성, 속도는 dy : 16
        alienShotSprites.add(alienShotSprite); // alienShotSprites ArrayList에 입력
        game.getSprites().add(alienShotSprite); //getSprites() 에 입력
    }

    private void spawnSpeedItem() {
        Random r = new Random();
        int speedItemDrop = r.nextInt(100) + 1; // 1 ~ 100 사이 랜덤 숫자를 speedItemDrop에 담고
        if (speedItemDrop <= 5) { // speedItemDrop 가 5 보다 작거나 같을 경우 if 문 실행 (5% 확률)
            int dx = r.nextInt(10) + 1; // dx 속도는 1 ~ 10
            int dy = r.nextInt(10) + 10; // dy  속도는 10 ~ 19
            game.getSprites().add(new SpeedItemSprite(context, game, (int)this.getX(),
                    (int)this.getY(), dx, dy)); // getSprites() 에 이 Item 의 x, y 좌표와 dx, dy 를 담음
        }
    }

    private void spawnPowerItem() {
        Random r = new Random();
        int powerItemDrop = r.nextInt(100) + 1; // 1 ~ 100 사이 랜덤 숫자를 powerItemDrop 에 담고
        if (powerItemDrop <= 3) { // powerItemDrop 가 3 보다 작거나 같을 경우 if 문 실행 (3% 확률)
            int dx = r.nextInt(10) + 1; // dx 속도는 1 ~ 10
            int dy = r.nextInt(10) + 10; // dy  속도는 10 ~ 19
            game.getSprites().add(new PowerItemSprite(context, game, (int)this.getX(),
                    (int)this.getY(), dx, dy)); // getSprites() 에 이 Item 의 x, y 좌표와 dx, dy 를 담음
        }
    }
    private void spawnHealItem() {
        Random r = new Random();
        int healItemDrop = r.nextInt(100) + 1; // 1 ~ 100 사이 랜덤 숫자를 healItemDrop 에 담고
        if (healItemDrop <= 1) { // healItemDrop 가 1 보다 작거나 같을 경우 if 문 실행 (1% 확률)
            int dx = r.nextInt(10) + 1; // dx 속도는 1 ~ 10
            int dy = r.nextInt(10) + 10; // dy  속도는 10 ~ 19
            game.getSprites().add(new HealitemSprite(context, game, (int) this.getX(),
                    (int) this.getY(), dx, dy)); // getSprites() 에 이 Item 의 x, y 좌표와 dx, dy 를 담음
        }
    }
}
