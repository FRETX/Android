package fretx.version4;

import java.util.ArrayList;

/*
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public class Constants {

    /*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
    public static final String COGNITO_POOL_ID = "eu-west-1:45b6bf1d-df66-4ad8-a460-ca5fdb7e3fc1";

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    public static final String BUCKET_NAME = "hwaccess";
    public static final String HW_BUCKET_MAPPING_FILE = "hwbucketmapping.csv";
    public static final String USER_INFO_FILE = "userinfo.csv";
    public static final String USER_HISTORY_FILE = "userhistory.csv";
    //public static final String NO_BT_DEVICE = "NOBTDEVICE";
    public static final String NO_BT_DEVICE = "D3:1C:FC:F9:EE:67";
    //public static final String NO_BT_BUCKET = "hw001";
    public static final String NO_BT_BUCKET = "hw003";
    public static boolean refreshed = false;
    public static ArrayList<SongItem> savedData;
}



