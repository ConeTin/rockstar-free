package ru.rockstar.client.features.impl.visuals;


import ru.rockstar.client.features.Category;
import ru.rockstar.client.features.Feature;
import ru.rockstar.client.ui.settings.impl.BooleanSetting;

public class NameProtect extends Feature {

    public static BooleanSetting otherNames;
    public static BooleanSetting skinSpoof;
    public static BooleanSetting tabSpoof;
    public static BooleanSetting scoreBoardSpoof;

    public NameProtect() {
        super("NameProtect", "��������� �������� ���������� � ���� � ������ ������� �� ����� ��� ������",0, Category.MISC);
        otherNames = new BooleanSetting("Other Names", true, () -> true);
        tabSpoof = new BooleanSetting("Tab Spoof", true, () -> true);
        skinSpoof = new BooleanSetting("Skin Spoof", true, () -> true);
        scoreBoardSpoof = new BooleanSetting("ScoreBoard Spoof", true, () -> true);
        addSettings(otherNames, tabSpoof, skinSpoof, scoreBoardSpoof);
    }
}