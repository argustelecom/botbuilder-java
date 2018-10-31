// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.builder.dialogs;

import com.microsoft.bot.builder.dialogs.choices.Token;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface TokenizerFunction
{
    List<Token> tokenize(String text, Optional<String> locale);
}
