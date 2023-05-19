package com.taewon.mygallag.sprites;


import android.content.Context;

import com.taewon.mygallag.SpaceInvadersView;

import java.util.Timer;
import java.util.TimerTask;

public class SpecialshotSprite extends Sprite{
    private SpaceInvadersView game;

    public SpecialshotSprite(Context context ,SpaceInvadersView game, int resId, float x, float y){
        super(context, resId, x, y);
        this.game = game;
        game.getPlayer().setSpecialShooting(true); // specialShooting 을 true 로 설정하여 필살기가 사용되도록 함
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoRemove();
            }
        }, 5000); // 5초 이후에 autoRemove() 로 필살기는 종료됨
    }

    public void move(){
        super.move();
        this.x = game.getPlayer().getX() - getWidth() + 240;
        // SpecialshotSprite의 x = 플레이어의 x 좌표 - 스프라이트 가로 길이 + 240
        this.y = game.getPlayer().getY() - getHeight();
        // SpecialshotSprite의 y = 플레이어의 y 좌표 - 스프라이트 세로 길이
        // 위와 같은 계산을 해야 하는 이유 : SpecialshotSprite 가 플레이어의 중심에서 나가도록 하기 위함
    }

    public void autoRemove(){
        game.getPlayer().setSpecialShooting(false); // SpecialShooting 을 false 로 되돌림
        game.removeSprite(this); // 스프라이트 제거
    }

}
