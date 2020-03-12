/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.appsearch.impl;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import com.google.android.icing.proto.IndexingConfig;
import com.google.android.icing.proto.PropertyConfigProto;
import com.google.android.icing.proto.SchemaProto;
import com.google.android.icing.proto.SchemaTypeConfigProto;
import com.google.android.icing.proto.TermMatchType;

import org.junit.Test;

public class AppSearchImplTest {
    private final Context mContext = InstrumentationRegistry.getContext();

    @Test
    public void testRewriteSchemaTypes() {
        SchemaProto inSchema = SchemaProto.newBuilder()
                .addTypes(SchemaTypeConfigProto.newBuilder()
                        .setSchemaType("TestType")
                        .addProperties(PropertyConfigProto.newBuilder()
                                .setPropertyName("subject")
                                .setDataType(PropertyConfigProto.DataType.Code.STRING)
                                .setCardinality(PropertyConfigProto.Cardinality.Code.OPTIONAL)
                                .setIndexingConfig(
                                        IndexingConfig.newBuilder()
                                                .setTokenizerType(
                                                        IndexingConfig.TokenizerType.Code.PLAIN)
                                                .setTermMatchType(TermMatchType.Code.PREFIX)
                                                .build()
                                ).build()
                        ).addProperties(PropertyConfigProto.newBuilder()
                                .setPropertyName("link")
                                .setDataType(PropertyConfigProto.DataType.Code.DOCUMENT)
                                .setCardinality(PropertyConfigProto.Cardinality.Code.OPTIONAL)
                                .setSchemaType("RefType")
                                .build()
                        ).build()
                ).build();

        SchemaProto expectedSchema = SchemaProto.newBuilder()
                .addTypes(SchemaTypeConfigProto.newBuilder()
                        .setSchemaType("com.android.server.appsearch.impl@42:TestType")
                        .addProperties(PropertyConfigProto.newBuilder()
                                .setPropertyName("subject")
                                .setDataType(PropertyConfigProto.DataType.Code.STRING)
                                .setCardinality(PropertyConfigProto.Cardinality.Code.OPTIONAL)
                                .setIndexingConfig(
                                        IndexingConfig.newBuilder()
                                                .setTokenizerType(
                                                        IndexingConfig.TokenizerType.Code.PLAIN)
                                                .setTermMatchType(TermMatchType.Code.PREFIX)
                                                .build()
                                ).build()
                        ).addProperties(PropertyConfigProto.newBuilder()
                                .setPropertyName("link")
                                .setDataType(PropertyConfigProto.DataType.Code.DOCUMENT)
                                .setCardinality(PropertyConfigProto.Cardinality.Code.OPTIONAL)
                                .setSchemaType("com.android.server.appsearch.impl@42:RefType")
                                .build()
                        ).build()
                ).build();

        AppSearchImpl impl = new AppSearchImpl(mContext);
        SchemaProto.Builder actualSchema = inSchema.toBuilder();
        impl.rewriteSchemaTypes("com.android.server.appsearch.impl@42:", actualSchema);

        assertThat(actualSchema.build()).isEqualTo(expectedSchema);
    }

    @Test
    public void testPackageNotFound() {
        AppSearchImpl impl = new AppSearchImpl(mContext);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> impl.setSchema(
                        /*callingUid=*/Integer.MAX_VALUE,
                        SchemaProto.getDefaultInstance(),
                        /*forceOverride=*/false));
        assertThat(e).hasMessageThat().contains("Failed to look up package name");
    }
}