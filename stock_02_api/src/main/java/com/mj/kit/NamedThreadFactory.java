/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mj.kit;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/21
 */
public class NamedThreadFactory implements ThreadFactory {
    protected static final AtomicInteger POOL_COUNTER = new AtomicInteger(1);
    protected final AtomicInteger mThreadCounter;
    protected final String mPrefix;
    protected final boolean mDaemon;
    protected final ThreadGroup mGroup;

    public NamedThreadFactory() {
        this("pool-" + POOL_COUNTER.getAndIncrement(), false);
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        this.mThreadCounter = new AtomicInteger(1);
        this.mPrefix = prefix + "-thread-";
        this.mDaemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.mGroup = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = this.mPrefix + this.mThreadCounter.getAndIncrement();
        Thread ret = new Thread(this.mGroup, runnable, name, 0L);
        ret.setDaemon(this.mDaemon);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return this.mGroup;
    }
}