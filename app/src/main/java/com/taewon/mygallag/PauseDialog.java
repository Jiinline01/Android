package com.taewon.mygallag;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class PauseDialog extends Dialog {
    RadioGroup bgMusicOnOff, effectSoundOnOff; // 배경 음악, 효과음 RadioGroup 을 가져옴

    public PauseDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.pause_dialog);
        bgMusicOnOff = findViewById(R.id.bgMusicOnOff);
        effectSoundOnOff = findViewById(R.id.effectSoundOnOff);
        init();
    }

    public void init() {
        bgMusicOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // bgMusicOnOff 이 체크되면
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.bgMusicOn: // bgMusicOn 이 체크 되었을 경우
                        MainActivity.bgMusic.setVolume(1, 1); // 배경 음악을 킴
                        break;
                    case R.id.bgMusicOff: // bgMusicOff 이 체크 되었을 경우
                        MainActivity.bgMusic.setVolume(0, 0); // 배경 음악을 끔
                        break;
                }
            }
        });
        effectSoundOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // effectSoundOnOff 이 체크되면
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.effectSoundOn: // effectSoundOn 이 체크되었을 경우
                        MainActivity.effectVolumn = 1.0f; // 효과음을 킴
                        break;
                    case R.id.effectSoundOff: // effectSoundOff이 체크되었을 경우
                        MainActivity.effectVolumn = 0; // 효과음을 끔
                        break;
                }
            }
        });
        findViewById(R.id.dialogCancelBtn).setOnClickListener(new View.OnClickListener() { // dialogCancelBtn 이 클릭되면
            @Override
            public void onClick(View view) {
                dismiss();
            } // 현재 Dialog를 종료
        }); // cancel 과의 차이 : dismiss를 사용해야 RadioGroup 에서 체크한 것이 적용되서 Dialog를 종료함

        findViewById(R.id.dialogOkBtn).setOnClickListener(new View.OnClickListener() { // dialogOkBtn 이 클릭되면
            @Override
            public void onClick(View view) {
                dismiss();
            } // 현재 Dialog를 종료
        });
    }
}
