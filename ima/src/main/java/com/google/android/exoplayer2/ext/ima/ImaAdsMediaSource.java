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
package com.google.android.exoplayer2.ext.ima;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.CompositeMediaSource;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;

/**
 * A {@link MediaSource} that inserts ads linearly with a provided content media source.
 *
 * @deprecated Use com.google.android.exoplayer2.source.ads.AdsMediaSource with ImaAdsLoader.
 */
@Deprecated
public final class ImaAdsMediaSource extends CompositeMediaSource<Void> {

  private final AdsMediaSource adsMediaSource;
  private Listener listener;

  /**
   * Constructs a new source that inserts ads linearly with the content specified by
   * {@code contentMediaSource}.
   *
   * @param contentMediaSource The {@link MediaSource} providing the content to play.
   * @param dataSourceFactory Factory for data sources used to load ad media.
   * @param imaAdsLoader The loader for ads.
   * @param adUiViewGroup A {@link ViewGroup} on top of the player that will show any ad UI.
   */
  public ImaAdsMediaSource(MediaSource contentMediaSource, DataSource.Factory dataSourceFactory,
                           ImaAdsLoader imaAdsLoader, ViewGroup adUiViewGroup) {
    this(contentMediaSource, dataSourceFactory, imaAdsLoader, adUiViewGroup, null, null);
  }

  /**
   * Constructs a new source that inserts ads linearly with the content specified by {@code
   * contentMediaSource}.
   *
   * @param contentMediaSource The {@link MediaSource} providing the content to play.
   * @param dataSourceFactory Factory for data sources used to load ad media.
   * @param imaAdsLoader The loader for ads.
   * @param adUiViewGroup A {@link ViewGroup} on top of the player that will show any ad UI.
   * @param eventHandler A handler for events. May be null if delivery of events is not required.
   * @param eventListener A listener of events. May be null if delivery of events is not required.
   */
  public ImaAdsMediaSource(
      MediaSource contentMediaSource,
      DataSource.Factory dataSourceFactory,
      ImaAdsLoader imaAdsLoader,
      ViewGroup adUiViewGroup,
      @Nullable Handler eventHandler,
      @Nullable AdsMediaSource.EventListener eventListener) {
    adsMediaSource = new AdsMediaSource(contentMediaSource, dataSourceFactory, imaAdsLoader,
        adUiViewGroup, eventHandler, eventListener);
  }

  @Override
  public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
    super.prepareSource(player, isTopLevelSource, listener);
    this.listener = listener;
    prepareChildSource(/* id= */ null, adsMediaSource);
  }

  @Override
  public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
    return adsMediaSource.createPeriod(id, allocator);
  }

  @Override
  public void releasePeriod(MediaPeriod mediaPeriod) {
    adsMediaSource.releasePeriod(mediaPeriod);
  }

  @Override
  protected void onChildSourceInfoRefreshed(
          Void id, MediaSource mediaSource, Timeline timeline, @Nullable Object manifest) {
    listener.onSourceInfoRefreshed(this, timeline, manifest);
  }
}
