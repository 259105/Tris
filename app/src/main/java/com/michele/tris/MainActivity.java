package com.michele.tris;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Switch mode;
    private TextView comment,result;
    private int[][] Mtris = {  // 1 := Croce , 2:= Testa
            new int[3],
            new int[3],
            new int[3]
    };
    private Button[][] buttons={
            new Button[3],
            new Button[3],
            new Button[3]
    };
    private Button replay;
    private boolean modCPU=true;  // true = CPU , false = Friend
    private int currPlayer,round=0;   // 1 = CROCE, 2 = CERCHIO
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       mode=findViewById(R.id.switchMod);
       comment=findViewById(R.id.comment);
       result=findViewById(R.id.result);
       replay=findViewById(R.id.replay);
       buttons[0][0]=findViewById(R.id.b00);
       buttons[0][1]=findViewById(R.id.b01);
       buttons[0][2]=findViewById(R.id.b02);
       buttons[1][0]=findViewById(R.id.b10);
       buttons[1][1]=findViewById(R.id.b11);
       buttons[1][2]=findViewById(R.id.b12);
       buttons[2][0]=findViewById(R.id.b20);
       buttons[2][1]=findViewById(R.id.b21);
       buttons[2][2]=findViewById(R.id.b22);
        resetGame();
        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) modCPU=true;
                else modCPU=false;
                resetGame();
            }
        });
    }

    private void resetGame(){
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                Mtris[i][j]=0;
        for(Button bv[]:buttons)
            for(Button b:bv){
                b.setEnabled(true);
                b.setText("");
            }
        round=0;
        result.setText("");
        replay.setEnabled(false);
        replay.setVisibility(View.INVISIBLE);
        if(new Random().nextBoolean()){
            comment.setText("Gioca Croce ( X )");
            currPlayer=1;
        }
        else {
            comment.setText("Gioca Cerchio ( O )");
            currPlayer=2;
            if(modCPU) playCPU();
        }
    }

    public void onClickTris(View v){
        round++;
        Button b=(Button) v;
        b.setEnabled(false);
        int id=b.getId();
        switch (id){
            case R.id.b00: Mtris[0][0]=currPlayer; break;
            case R.id.b01: Mtris[0][1]=currPlayer; break;
            case R.id.b02: Mtris[0][2]=currPlayer; break;
            case R.id.b10: Mtris[1][0]=currPlayer; break;
            case R.id.b11: Mtris[1][1]=currPlayer; break;
            case R.id.b12: Mtris[1][2]=currPlayer; break;
            case R.id.b20: Mtris[2][0]=currPlayer; break;
            case R.id.b21: Mtris[2][1]=currPlayer; break;
            case R.id.b22: Mtris[2][2]=currPlayer; break;
        }
        if(currPlayer==1){ b.setText("X"); b.setTextColor(Color.RED);}
        else{b.setText("O"); b.setTextColor(Color.BLUE);}

        if(checkEnd()) return;

        currPlayer=(currPlayer+1)%3;
        if(currPlayer==0) currPlayer++;
        if(currPlayer==1) comment.setText("Gioca Croce ( X )");
        else comment.setText("Gioca Cerchio ( O )");

        if(modCPU){
            for(Button bv[]:buttons)
                for(Button bu:bv)
                    bu.setEnabled(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playCPU();
                        }
                    });
                }
            }).start();
        }
    }

    public void onClickReplay(View v){
        resetGame();
    }

    private boolean checkEnd(){
        int winner=findWinner(Mtris);
       // Log.d("Winner",String.valueOf(winner));
        if(winner!=0){
            comment.setText("");
            if(winner==1) result.setText("Ha vinto CROCE ( X )");
            else result.setText("Ha vinto CERCHIO ( O )");
            endGameView();
            return true;
        }

        if(round==9){
            comment.setText("");
            result.setText("PARI");
            endGameView();
            return true;
        }
        return false;
    }

    private void endGameView(){
        replay.setVisibility(View.VISIBLE);
        replay.setEnabled(true);
        for(Button bv[]:buttons)
            for(Button b:bv)
                b.setEnabled(false);
    }

    private int findWinner(int M[][]){
        /*String s="\n"+M[0][0]+" "+M[0][1]+" "+M[0][2]+"\n"+
                M[1][0]+" "+M[1][1]+" "+M[1][2]+"\n"+
                M[2][0]+" "+M[2][1]+" "+M[2][2]+"\n";
        Log.d("Matrice",s);

         */
        for(int i=0;i<3;i++){
            if(M[i][0]==M[i][1] && M[i][0]==M[i][2] && M[i][0]!=0)
                return M[i][0];
            if(M[0][i]==M[1][i] && M[0][i]==M[2][i] && M[0][i]!=0)
                return M[0][i];
        }
        if((M[0][0]==M[1][1] && M[0][0]==M[2][2]
            || M[0][2]==M[1][1] && M[0][2]==M[2][0]) && M[1][1]!=0)
            return M[1][1];
        return 0;
    }

    private void findWinner(int M[][],IntWrapper nSolX,IntWrapper nSolO){
        /*String s="\n"+M[0][0]+" "+M[0][1]+" "+M[0][2]+"\n"+
                M[1][0]+" "+M[1][1]+" "+M[1][2]+"\n"+
                M[2][0]+" "+M[2][1]+" "+M[2][2]+"\n";
        Log.d("Matrice",s);

         */
        for(int i=0;i<3;i++){
            if(M[i][0]==M[i][1] && M[i][0]==M[i][2] && M[i][0]!=0)
                if(M[i][0]==1) nSolX.a++;
                else nSolO.a++;
            if(M[0][i]==M[1][i] && M[0][i]==M[2][i] && M[0][i]!=0)
                if(M[0][i]==1) nSolX.a++;
                else nSolO.a++;
        }
        if((M[0][0]==M[1][1] && M[0][0]==M[2][2]
                || M[0][2]==M[1][1] && M[0][2]==M[2][0]) && M[1][1]!=0)
            if(M[1][1]==1) nSolX.a++;
            else nSolO.a++;
    }

    public class IntWrapper {
        public int a;
        public IntWrapper(int a){ this.a = a;}
    }

    private void playCPU(){
        IntWrapper bx=new IntWrapper(-1),by=new IntWrapper(-1),bpath=new IntWrapper(10),bNsols=new IntWrapper(0);
        if(round==0){
            bx.a=1;by.a=1;
        }else if(round==1 && Mtris[1][1]==0){
            bx.a=1;by.a=1;
        }else{
            int Msol[][]=new int[3][];
            int x=0,y=0,currpath=0;

            for(int i=0;i<3;i++)
                Msol[i]=new int[3];
            for(int i=0;i<3;i++)
                for(int j=0;j<3;j++)
                    Msol[i][j]=Mtris[i][j];
                playCPUR(bx,by,Msol,bpath,currpath,x,y,2,1,bNsols); // difesa
            if(bx.a==-1 || by.a==-1)
                playCPUR(bx,by,Msol,bpath,currpath,x,y,1,2,bNsols); // attacco
            if(bx.a==-1 || by.a==-1)
                for(int i=0;i<3;i++)
                    for(int j=0;j<3;j++)
                        if(Mtris[i][j]==0){
                            bx.a=i;by.a=j;
                        }
        }
        round++;
        buttons[bx.a][by.a].setEnabled(false);
        buttons[bx.a][by.a].setText("O");
        buttons[bx.a][by.a].setTextColor(Color.BLUE);
        Mtris[bx.a][by.a]=2;
        if(checkEnd()) return;
        currPlayer=(currPlayer+1)%3;
        if(currPlayer==0) currPlayer++;
        if(currPlayer==1) comment.setText("Gioca Croce ( X )");
        else comment.setText("Gioca Cerchio ( O )");
        for(int i=0;i<3;i++)
            for (int j=0;j<3;j++)
                if(Mtris[i][j]==0)
                    buttons[i][j].setEnabled(true);
    }

    private void playCPUR(IntWrapper bx,IntWrapper by,int[][] Msol,IntWrapper bpath,int currpath,int x,int y,int prec,int vinc,IntWrapper bNSols){

        for(int k=0;k<9;k++){
            int i=k/3,j=k%3;
            if(Msol[i][j]!=0) continue;
            if(currpath==0) {x=i;y=j;}
            if(prec==1){
                Msol[i][j]=2;
                playCPUR(bx,by,Msol,bpath,currpath+1,x,y,2,vinc,bNSols);
            }else{
                Msol[i][j]=1;
                int sconf=(vinc+1)%3;
                if(sconf==0) sconf++;
                if(findWinner(Msol)!=sconf) playCPUR(bx,by,Msol,bpath,currpath+1,x,y,1,vinc,bNSols);
            }
            Msol[i][j]=0;
        }
        IntWrapper nSols[]={
           new IntWrapper(0),
           new IntWrapper(0)
        } ;
        findWinner(Msol,nSols[0],nSols[1]);
        if(nSols[vinc-1].a>0){
            if(currpath<bpath.a && Mtris[x][y]==0 && nSols[vinc-1].a>bNSols.a){
                bx.a=x; by.a=y;
                bpath.a=currpath;
               /*String s="\n"+Msol[0][0]+" "+Msol[0][1]+" "+Msol[0][2]+"\n"+
                        Msol[1][0]+" "+Msol[1][1]+" "+Msol[1][2]+"\n"+
                        Msol[2][0]+" "+Msol[2][1]+" "+Msol[2][2]+"\n";
                Log.d("Possibile Soluzione",s);
                Log.d("Salto" ,"\n");*/
            }
        }
    }

}