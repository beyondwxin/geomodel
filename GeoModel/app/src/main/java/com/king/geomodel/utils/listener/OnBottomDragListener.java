/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.king.geomodel.utils.listener;

/**
 * Created by king on 2016/9/9.
 * 拖拽View底部的回调接口
 */
public interface OnBottomDragListener {

    /**
     * @param rightToLeft ？从右向左 : 从左向右
     */
    void onDragBottom(boolean rightToLeft);
}