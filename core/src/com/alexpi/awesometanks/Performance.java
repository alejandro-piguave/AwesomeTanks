package com.alexpi.awesometanks;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.alexpi.awesometanks.utils.Styles;

/**
 * Created by Alex on 02/02/2016.
 */
public class Performance extends Group {
    private ProgressBar bar;
    private Label nameLabel;
    private TextButton buy;
    private int price;
    public Performance(String name, Skin skin, int fontSize, float value,float max, int price){
        nameLabel = new Label(name, Styles.getLabelStyle(fontSize));nameLabel.setAlignment(Align.center);
        buy = new TextButton("Buy "+price+" $", Styles.getTextButtonStyle(fontSize));
        bar = new ProgressBar(0f,max,1f,false,skin);bar.setValue(value);

        addActor(bar);
        addActor(nameLabel);
        addActor(buy);

        this.price = price;
    }
    public void changePrice(int price){this.price=price;buy.setText("Buy " + price + " $");}
    @Override
    public void setBounds(float x, float y, float width, float height) {
        bar.setBounds(x, y + (height / 3f) * 2f, width, height / 3f);
        nameLabel.setBounds(x, y + height / 3f, width, height / 3f);
        buy.setBounds(x, y, width, height / 3f);
    }
    public boolean canBuy(int money){
        return (money >=price && bar.getValue() < bar.getMaxValue());}

    public int getPrice(){return price;}
    public TextButton getBuyButton() {return buy;}
    public String getName(){return nameLabel.getText().toString();}
    public void increaseValue(int i) {bar.setValue(bar.getValue()+i);}
    public void setValue(float value){bar.setValue(value);}
    public int getValue(){return (int) bar.getValue();}

    public float getMaxValue() {return bar.getMaxValue();}
}
