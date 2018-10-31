package com.microsoft.bot.builder.dialogs.choices;

public class ChoiceTuple {

    protected Choice _c1;
    protected Choice _c2;

    public ChoiceTuple(String c1, String c2) {
        _c1 = new Choice();
        _c1.setValue(c1);
        _c2 = new Choice();
        _c2.setValue(c2);
    }

    public Choice item1() { return _c1; }
    public Choice item2() { return _c2; }
}
