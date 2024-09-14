package com.alexpi.awesometanks.screens.upgrades;

import com.alexpi.awesometanks.screens.widget.GameProgressBar;
import com.alexpi.awesometanks.screens.widget.Styles;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class UpgradeTable extends Table {

    private GameProgressBar bar;
    private Label nameLabel;
    private TextButton buy;
    private int price;
    public UpgradeTable(AssetManager assetManager, String name, float value, float max, int price, boolean vertical){
        this.price = price;
        nameLabel = new Label(name, Styles.getLabelStyleSmall(assetManager));
        nameLabel.setAlignment(Align.center);
        buy = new TextButton("Buy "+price+" $", Styles.getTextButtonStyleSmall(assetManager));
        bar = new GameProgressBar(assetManager, max,0f, vertical);
        bar.setValue(value);
        Cell<GameProgressBar> barCell = add(bar);
        if(vertical) barCell.expandY().fillY();
        else barCell.fillX();
        barCell.row();
        add(nameLabel).padTop(8).padBottom(8).row();
        add(buy).row();

    }
    public UpgradeTable(AssetManager assetManager, String name, float value, float max, int price){
        this(assetManager, name, value, max, price, false);
    }

    public void changePrice(int price){this.price=price;buy.setText("Buy " + price + " $");}

    public boolean canBuy(int money){
        return (money >=price && bar.getValue() < bar.getMaxValue());
    }

    public int getPrice(){return price;}
    public TextButton getBuyButton() {return buy;}
    public String getName(){return nameLabel.getText().toString();}
    public void increaseValue(int i) {
        if(bar.getValue() + i > bar.getMaxValue()){
            bar.setValue(bar.getMaxValue());
        } else bar.setValue(bar.getValue()+i);
    }
    public void setValue(float value){bar.setValue(value);}
    public int getValue(){return (int) bar.getValue();}

    public boolean isMaxValue(){return bar.getValue() == bar.getMaxValue();}

    public float getMaxValue() {return bar.getMaxValue();}
}
