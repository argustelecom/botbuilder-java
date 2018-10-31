// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.schema;

public class ResultPair<X, Y> {
    public final X x;
    public final Y y;
    public ResultPair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
