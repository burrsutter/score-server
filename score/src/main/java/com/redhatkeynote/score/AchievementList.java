/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhatkeynote.score;

import java.util.ArrayList;
import java.util.List;

public class AchievementList {

    private List<Achievement> achievements = new ArrayList<Achievement>();

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public void addAchievement( Achievement a ) {
        this.achievements.add( a );
    }

    public boolean hasAchievement( Achievement a ) {
        if (a != null) {
            final String desc = a.getDescription();
            for(Achievement current: achievements) {
                if (current.getDescription().equals(desc)) {
                    return true;
                }
            }
        }
        return false;
    }
}
