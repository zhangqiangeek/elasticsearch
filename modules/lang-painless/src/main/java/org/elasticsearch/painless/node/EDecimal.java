/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.painless.node;

import org.elasticsearch.painless.Location;
import org.elasticsearch.painless.Scope;
import org.elasticsearch.painless.ir.ClassNode;
import org.elasticsearch.painless.ir.ConstantNode;
import org.elasticsearch.painless.ir.ExpressionNode;
import org.elasticsearch.painless.symbol.ScriptRoot;

import java.util.Objects;

/**
 * Represents a decimal constant.
 */
public final class EDecimal extends AExpression {

    private final String value;

    protected Object constant;

    public EDecimal(Location location, String value) {
        super(location);

        this.value = Objects.requireNonNull(value);
    }

    @Override
    void analyze(ScriptRoot scriptRoot, Scope scope) {
        if (!read) {
            throw createError(new IllegalArgumentException("Must read from constant [" + value + "]."));
        }

        if (value.endsWith("f") || value.endsWith("F")) {
            try {
                constant = Float.parseFloat(value.substring(0, value.length() - 1));
                actual = float.class;
            } catch (NumberFormatException exception) {
                throw createError(new IllegalArgumentException("Invalid float constant [" + value + "]."));
            }
        } else {
            String toParse = value;
            if (toParse.endsWith("d") || value.endsWith("D")) {
                toParse = toParse.substring(0, value.length() - 1);
            }
            try {
                constant = Double.parseDouble(toParse);
                actual = double.class;
            } catch (NumberFormatException exception) {
                throw createError(new IllegalArgumentException("Invalid double constant [" + value + "]."));
            }
        }
    }

    @Override
    ExpressionNode write(ClassNode classNode) {
        ConstantNode constantNode = new ConstantNode();
        constantNode.setLocation(location);
        constantNode.setExpressionType(actual);
        constantNode.setConstant(constant);

        return constantNode;
    }

    @Override
    public String toString() {
        return singleLineToString(value);
    }
}
