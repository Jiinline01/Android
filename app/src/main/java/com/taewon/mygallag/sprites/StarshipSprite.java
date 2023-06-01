package com.taewon.mygallag.sprites;

import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;

public class StarshipSprite extends Sprite{
    Context context;
    SpaceInvadersView game;
    public float speed;
    private int bullets, life = 3, powerLevel;
    private int specialShotCount;
    private boolean isSpecialShooting;
    private static ArrayList<Integer> bulletSprites = new ArrayList<Integer>();
    private final static float MAX_SPEED = 3.5f;
    private final static int MAX_HEART = 3;
    //    ArrayList<Integer> effectSoundIdList;
    private RectF rectF;
    private boolean isReloading = false;

    public StarshipSprite(Context context, SpaceInvadersView game, int resId, int x, int y, float speed){
        super(context, resId, x, y);
        this.context = context;
        this.game = game;
        this.speed = speed;
        init();
    }

    public void init(){ // 초기화
        dx = dy = 0; // 속도 0
        bullets = 30; // 총알 30
        life = 3; // 목숨 3
        specialShotCount = 3; // 필살기 3
        powerLevel = 0; // 파워레벨 0
        Integer [] shots = {R.drawable.shot_001, R.drawable.shot_002, R.drawable.shot_003, R.drawable.shot_004, R.drawable.shot_005, R.drawable.shot_006, R.drawable.shot_007};
        // shots 배열에 shot_001..2.. 추가
        for(int i = 0; i < shots.length; i++){
            bulletSprites.add(shots[i]); // bullerSprites ArrayList 에 총알 이미지 순서대로 추가
        }
    }

    public void move(){
        // 벽에 부딪히면 못 가게 하기
        if((dx < 0) && (x < 0)) return;
        // dx 속도가 0 보다 작을 경우 (왼쪽으로 움직일 경우) && x 좌표가 120 보다 작을 경우(왼쪽 벽에 붙어있을 경우) move() 메소드를 종료한다
        if((dx > 0) && (x > game.screenW - 130)) return;
        // dx 속도가 0 보다 클 경우 (오른쪽으로 움직일 경우) && x 좌표가 스크린 오른쪽 끝 - 120 보다 클 경우(오른쪽 벽에 붙어있을 경우) move() 메소드를 종료한다
        if((dy < 0) && (y < 120)) return;
        // dy 속도가 0 보다 작을 경우 (위쪽으로 움직일 경우) && y 좌표가 120 보다 작을 경우(위쪽 벽에 붙어있을 경우) move() 메소드를 종료한다
        if((dy > 0) && (y > game.screenH - 180)) return;
        // dy 속도가 0 보다 클 경우 (아래쪽으로 움직일 경우) && x 좌표가 스크린 아레쪽 끝 - 120 보다 클 경우(아래쪽 벽에 붙어있을 경우) move() 메소드를 종료한다
        super.move();
    }

    // 총알 개수 리턴
    public int getBulletsCount(){return bullets;} // 총알 개수 리턴
    // 위, 아래, 오른쪽, 왼쪽 이동하기
    public void moveRight(double force){setDx((float)(1*force*speed));}
    public void moveLeft(double force){setDx((float)(-1*force*speed));}
    public void moveDown(double force){setDy((float)(1*force*speed));}
    public void moveUp(double force){setDy((float)(-1*force*speed));}

    public void resetDx(){setDx(0);}
    public void resetDy(){setDy(0);}

    // 스피드 제어
    public void plusSpeed(float speed){this.speed += speed;} // this.speed + speed 의 값을 this.speed 에 넣어주기

    // 총알 발사
    public void fire(){
        if (isReloading | isSpecialShooting){return;} // 재장전 중 이거나 스킬을 사용하고 있는 도중 fire 를 호출 하면 리턴
        MainActivity.effectSound(MainActivity.PLAYER_SHOT); // MainActivity 의 effectSound(PLAYER_SHOT) 실행
        // ShotSprite 생성자 구현
        ShotSprite shot = new ShotSprite(context, game, bulletSprites.get(powerLevel), getX() + 10, getY() - 30, -16);
        // SpaceInvadersView 의 getSprites() 구현
        game.getSprites().add(shot);
        bullets --; // 총알 개수 차감

        MainActivity.bulletCount.setText(bullets + "/30"); // bulletCount 에 차감 된 bullets + "/30" 을 실행
        Log.d("bullets", bullets + "/30");
        if(bullets == 0){ // 총알 개수가 0이면 재장전(reloadBullets())을 호출하고 리턴
            reloadBullets();
            return;
        }
    }

    public void powerUp(){ // PowerItem 을 먹을 때 // bulletSprites ArrayList.size() = 7
        if(powerLevel >= bulletSprites.size() - 1){ // 현재 Starship 의 powerLevel 이 bulletSprites ArrayList 의 사이즈 -1한 값 보다 크거나 같아질 경우
            game.setScore(game.getScore() + 1); // 더 이상 총알 업그레이드가 아닌 score 에서 점수 1점 추가
            MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // 추가한 score 를 MainActivity 의 scoreTv 에 출력
            return;
        }
        powerLevel++; // 파워업
        MainActivity.fireBtn.setImageResource(bulletSprites.get(powerLevel)); // MainActivity 의 fireBtn(발사 버튼) 을 bulletSprites ArrayList 에서 powerLevel 위치의 배열 불러오기(이미지)
        MainActivity.fireBtn.setBackgroundResource(R.drawable.round_button_shape); // MainActivity 의 fireBtn(발사 버튼) 의 배경 호출
    }

    // 총알 다시 세팅
    public void reloadBullets(){ // 재장전
        isReloading = true; // 원래 false 상태 였는데 true 상태로 변경
        MainActivity.effectSound(MainActivity.PLAYER_RELOAD); // MainActivity의 재장전 효과음
        MainActivity.fireBtn.setEnabled(false); // 발사 버튼 선택 안되게 하기
        MainActivity.reloadBtn.setEnabled(false); // 재장전 버튼 선택 안되게 하기
        // Thread sleep 사용하지 않고 지연시키는 클래스
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bullets = 30; // 총알 개수 30
                MainActivity.fireBtn.setEnabled(true); // 발사 버튼 선택 가능
                MainActivity.reloadBtn.setEnabled(true); // 재장전 버튼 선택 가능
                MainActivity.bulletCount.setText(bullets + "/30"); // 재장전한 bullet 값 "/30" 과 같이 출력
                MainActivity.bulletCount.invalidate(); // 화면 새로고침
                isReloading = false; // 다시 false 로 변경
            }
        }, 2000); // 2초후에 실행
    }

    // 필사기
    public void specialShot(){
        specialShotCount --; // 필사기 사용 개수 -1
        SpecialshotSprite shot = new SpecialshotSprite(context, game, R.drawable.laser, getRect().right - getRect().left, 0); // SpecialshotSprite 구현
        game.getSprites().add(shot); // game -> SpaceInvadersView 의 getSprites() : sprite 에 shot 추가하기
    }

    public int getSpecialShotCount(){return specialShotCount;} // 호출 시 specialCount 반환

    public boolean isSpecialShooting(){return isSpecialShooting;} // 호출 시 isSpecialShooting 반환

    public void setSpecialShooting(boolean specialShooting){isSpecialShooting = specialShooting;}

    public int getLife(){return life;} // 호출 시 life 반환

    public void hurt(){ // 공격 당하거나 적 비행기와 충돌 시
        life--; // life -1
        if(life <= 0){ // life 가 0이하가 되면
            ((ImageView)MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_border_24);
            game.endGame(); // SpaceInvadersView 의 endGame() 에서 game 종료시키기
            return;
        }
        Log.d("hurt", Integer.toString(life)); // 생명 확인하기
        ((ImageView)MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    public void heal(){ // 생명 얻었을 때
        Log.d("heal", Integer.toString(life));
        if(life + 1 > MAX_HEART){ // life + 1이 MAX_HEART(3) 값보다 커질 경우
            game.setScore(game.getScore() + 1); // score + 1점
            MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // MainActivity 의 scoreTv 에 추가 된 점수 넣기
            return;
        }
        ((ImageView)MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_24);
        life++; // life + 1
    }

    private void speedUp() { // 속도 올리기
        if(MAX_SPEED >= speed + 0.2f) plusSpeed(0.2f); // MAX_SPEED(3.5f) 보다 speed 가 작거나 같으면 speed 0.2f만큼 추가
        else{ // MAX_SPEED = speed 가 되면
            game.setScore(game.getScore() + 1); // 점수 1점 추가
            MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // MainActivity 의 scoreTv 에 추가 된 점수 넣기
        }
    }

    // Sprite 의 handleCollision() -> 충돌처리
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            // Alien 아이템이면
            game.removeSprite(other); // Alien 아이템 삭제
            MainActivity.effectSound(MainActivity.PLAYER_HURT); // PLAYER_HURT 효과음 출력
            hurt(); // hurt 호출
        }
        if (other instanceof SpeedItemSprite) {
            // 스피드 아이템이면
            game.removeSprite(other); // SpeedItem 삭제
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM); // PLAYER_GET_ITEM 효과음 출력
            speedUp(); // speedUp 호출
        }
        if (other instanceof AlienShotSprite) {
            // 총알 맞으면
            game.removeSprite(other); // AlienShot 삭제
            MainActivity.effectSound(MainActivity.PLAYER_HURT); // PLAYER_HURT 효과음 출력
            hurt(); // hurt 호출
        }
        if (other instanceof PowerItemSprite) {
            // 아이템 맞으면
            game.removeSprite(other); // powerItem 삭제
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM); // PLAYER_GET_ITEM 효과음 출력
            powerUp(); // powerUp 호출
        }
        if  (other instanceof HealitemSprite){
            // 생명 아이템 맞으면
            game.removeSprite(other); // Healitem 삭제
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM); // PLAYER_GET_ITEM 효과음 출력
            heal(); // heal 호출
        }
    }

    public int getPowerLevel(){return powerLevel;} // 호출 시 powerLevel 반환

}
