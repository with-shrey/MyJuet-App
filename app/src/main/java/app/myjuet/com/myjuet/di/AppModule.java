/*
 * Copyright (C) 2017 The Android Open Source Project
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

package app.myjuet.com.myjuet.di;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.database.AttendenceDetailsDao;
import app.myjuet.com.myjuet.database.DateSheetDao;
import app.myjuet.com.myjuet.database.ExamMarksDao;
import app.myjuet.com.myjuet.database.SeatingPlanDao;
import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    AppDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, AppDatabase.class, "myjuet.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
    }

    @Singleton @Provides
    AttendenceDataDao provideAttendenceDao(AppDatabase db) {
        return db.AttendenceDao();
    }

    @Singleton @Provides
    AttendenceDetailsDao provideAttendenceDetailsDao(AppDatabase db) {
        return db.AttendenceDetailsDao();
    }
    @Singleton @Provides
    DateSheetDao provideDateSheetDao(AppDatabase db) {
        return db.DateSheetDao();
    }
    @Singleton @Provides
    SeatingPlanDao provideSeatingPlanDao(AppDatabase db) {
        return db.SeatingPlanDao();
    }
    @Singleton @Provides
    ExamMarksDao provideExamMarksDao(AppDatabase db) {
        return db.ExamMarksDao();
    }

    @Singleton @Provides
    Context provideContext(Application app) {
        return app.getApplicationContext();
    }
}
