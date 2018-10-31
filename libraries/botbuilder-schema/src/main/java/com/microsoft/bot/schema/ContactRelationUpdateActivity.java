// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.schema;

import com.microsoft.bot.schema.models.Activity;

public class ContactRelationUpdateActivity extends Activity {
    /**
     * add|remove
     */
    private String _action;

    public String getAction() {
        return _action;
    }

    public void setAction(String action) {
        this._action = action;
    }
}
