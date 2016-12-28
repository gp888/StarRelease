package com.xingfabu.direct.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import com.xingfabu.direct.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by UKfire on 16/3/14.
 */
public class SmileManager {
    private static List<Smile> smileList;
    private static SparseArray<Smile> smileMap;
    private static SparseArray<Drawable> drawableMap;
    private static Context context;

    public SmileManager(Context context) {
        this.context = context;
    }

    public static SparseArray<Drawable> getDrawableMap(Context context) {
        if (drawableMap == null) {
            drawableMap = new SparseArray<>();
            List<Smile> smileList = getSmileList();
            float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            int sp = (int) (20 * fontScale + 0.5f);
            for (Smile smile : smileList) {
                Drawable drawable = context.getResources().getDrawable(smile.getResId());
                drawable.setBounds(0, 0, sp, sp);
                drawableMap.put(smile.getResId(), drawable);
            }
        }
        return drawableMap;
    }

    public static List<Smile> getSmileList() {
        if(smileList==null){
            smileList=new ArrayList<>();
            smileList.add(new Smile(R.drawable.emotion_1001,"[微笑]"));
            smileList.add(new Smile(R.drawable.emotion_1002,"[憋嘴]"));
            smileList.add(new Smile(R.drawable.emotion_1003,"[色]"));
            smileList.add(new Smile(R.drawable.emotion_1004,"[发呆]"));
            smileList.add(new Smile(R.drawable.emotion_1005,"[得意]"));
            smileList.add(new Smile(R.drawable.emotion_1006,"[流泪]"));
            smileList.add(new Smile(R.drawable.emotion_1007,"[害羞]"));
            smileList.add(new Smile(R.drawable.emotion_1008,"[闭嘴]"));
            smileList.add(new Smile(R.drawable.emotion_1009,"[睡]"));
            smileList.add(new Smile(R.drawable.emotion_1010,"[大哭]"));
            smileList.add(new Smile(R.drawable.emotion_1011,"[尴尬]"));
            smileList.add(new Smile(R.drawable.emotion_1012,"[发怒]"));
            smileList.add(new Smile(R.drawable.emotion_1013,"[调皮]"));
            smileList.add(new Smile(R.drawable.emotion_1014,"[呲牙]"));
            smileList.add(new Smile(R.drawable.emotion_1015,"[惊讶]"));
            smileList.add(new Smile(R.drawable.emotion_1016,"[难过]"));
            smileList.add(new Smile(R.drawable.emotion_1017,"[酷]"));
            smileList.add(new Smile(R.drawable.emotion_1018,"[冷汗]"));
            smileList.add(new Smile(R.drawable.emotion_1019,"[抓狂]"));
            smileList.add(new Smile(R.drawable.emotion_1020,"[吐]"));
            smileList.add(new Smile(R.drawable.emotion_1021,"[偷笑]"));
            smileList.add(new Smile(R.drawable.emotion_1022,"[愉快]"));
            smileList.add(new Smile(R.drawable.emotion_1023,"[白眼]"));
            smileList.add(new Smile(R.drawable.emotion_1024,"[傲慢]"));
            smileList.add(new Smile(R.drawable.emotion_1025,"[饥饿]"));
            smileList.add(new Smile(R.drawable.emotion_1026,"[困]"));
            smileList.add(new Smile(R.drawable.emotion_1027,"[惊恐]"));
            smileList.add(new Smile(R.drawable.emotion_1028,"[流汗]"));
            smileList.add(new Smile(R.drawable.emotion_1029,"[憨笑]"));
            smileList.add(new Smile(R.drawable.emotion_1030,"[悠闲]"));
            smileList.add(new Smile(R.drawable.emotion_1031,"[奋斗]"));
            smileList.add(new Smile(R.drawable.emotion_1032,"[咒骂]"));
            smileList.add(new Smile(R.drawable.emotion_1033,"[疑问]"));
            smileList.add(new Smile(R.drawable.emotion_1034,"[嘘]"));
            smileList.add(new Smile(R.drawable.emotion_1035,"[晕]"));
            smileList.add(new Smile(R.drawable.emotion_1036,"[疯了]"));
            smileList.add(new Smile(R.drawable.emotion_1037,"[衰]"));
            smileList.add(new Smile(R.drawable.emotion_1038,"[骷髅]"));
            smileList.add(new Smile(R.drawable.emotion_1039,"[敲打]"));
            smileList.add(new Smile(R.drawable.emotion_1040,"[再见]"));
            smileList.add(new Smile(R.drawable.emotion_1041,"[擦汗]"));
            smileList.add(new Smile(R.drawable.emotion_1042,"[抠鼻]"));
            smileList.add(new Smile(R.drawable.emotion_1043,"[鼓掌]"));
            smileList.add(new Smile(R.drawable.emotion_1044,"[糗大了]"));
            smileList.add(new Smile(R.drawable.emotion_1045,"[坏笑]"));
            smileList.add(new Smile(R.drawable.emotion_1046,"[左哼哼]"));
            smileList.add(new Smile(R.drawable.emotion_1047,"[右哼哼]"));
            smileList.add(new Smile(R.drawable.emotion_1048,"[哈欠]"));
            smileList.add(new Smile(R.drawable.emotion_1049,"[鄙视]"));
            smileList.add(new Smile(R.drawable.emotion_1050,"[委屈]"));
            smileList.add(new Smile(R.drawable.emotion_1051,"[快哭了]"));
            smileList.add(new Smile(R.drawable.emotion_1052,"[阴险]"));
            smileList.add(new Smile(R.drawable.emotion_1053,"[亲亲]"));
            smileList.add(new Smile(R.drawable.emotion_1054,"[吓]"));
            smileList.add(new Smile(R.drawable.emotion_1055,"[可怜]"));
            smileList.add(new Smile(R.drawable.emotion_1056,"[菜刀]"));
            smileList.add(new Smile(R.drawable.emotion_1057,"[西瓜]"));
            smileList.add(new Smile(R.drawable.emotion_1058,"[啤酒]"));
            smileList.add(new Smile(R.drawable.emotion_1059,"[篮球]"));
            smileList.add(new Smile(R.drawable.emotion_1060,"[兵乓]"));
            smileList.add(new Smile(R.drawable.emotion_1061,"[红包]"));
        }
        return smileList;
    }

    public static SparseArray<Smile> getSmileMap(){
        if(smileMap==null)
        {
            smileMap=new SparseArray<>();
            List<Smile> list=getSmileList();
            for (Smile smile: list) {
                smileMap.put(smile.getTag().hashCode(),smile);
            }
        }
        return smileMap;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
