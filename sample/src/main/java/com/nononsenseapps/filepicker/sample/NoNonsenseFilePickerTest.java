/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class NoNonsenseFilePickerTest extends NoNonsenseFilePicker {

    @Override
    protected void onResume() {
        super.onResume();

        // Request permission
        if (hasPermission()) {
            try {
                createTestData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            requestPermission();
        }
    }

    void createTestData() throws IOException {
        File sdRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();

        File testRoot = new File(sdRoot, "000000_nonsense-tests");

        testRoot.mkdir();
        assertTrue("Failed to create directory", testRoot.isDirectory());

        List<File> subdirs = Arrays.asList(new File(testRoot, "A-dir"),
                new File(testRoot, "B-dir"),
                new File(testRoot, "C-dir"));


        for (File subdir : subdirs) {
            subdir.mkdir();
            assertTrue("Failed to create sub directory", subdir.isDirectory());

            for (int sf = 0; sf < 10; sf++) {
                File subfile = new File(subdir, "file-" + sf + ".txt");

                subfile.createNewFile();

                assertTrue("Failed to create file", subfile.isFile());
            }
        }
    }

    protected boolean hasPermission() {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new RequestPermission(), isGranted -> {
                if (isGranted) {
                    try {
                        createTestData();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
}
