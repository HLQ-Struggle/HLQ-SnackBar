package com.hlq.snackbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Snackbars provide lightweight feedback about an operation. They show a brief message at the
 * bottom of the screen on mobile and lower left on larger devices. Snackbars appear above all other
 * elements on screen and only one can be displayed at a time.
 * <p>
 * They automatically disappear after a timeout or after user interaction elsewhere on the screen,
 * particularly after interactions that summon a new surface or activity. Snackbars can be swiped
 * off screen.
 * <p>
 * Snackbars can contain an action which is set via
 * {@link #setAction(CharSequence, View.OnClickListener)}.
 * <p>
 * To be notified when a snackbar has been shown or dismissed, you can provide a {@link Callback}
 * via {@link BaseTransientBottomBar#addCallback(BaseCallback)}.</p>
 */
public final class NiceSnackBar extends BaseTransientBottomBar<NiceSnackBar> {

    /**
     * Show the Snackbar indefinitely. This means that the Snackbar will be displayed from the time
     * that is {@link #show() shown} until either it is dismissed, or another Snackbar is shown.
     *
     * @see #setDuration
     */
    public static final int LENGTH_INDEFINITE = BaseTransientBottomBar.LENGTH_INDEFINITE;

    /**
     * Show the Snackbar for a short period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = BaseTransientBottomBar.LENGTH_SHORT;

    /**
     * Show the Snackbar for a long period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_LONG = BaseTransientBottomBar.LENGTH_LONG;

    /**
     * Callback class for {@link NiceSnackBar} instances.
     * <p>
     * Note: this class is here to provide backwards-compatible way for apps written before
     * the existence of the base {@link BaseTransientBottomBar} class.
     *
     * @see BaseTransientBottomBar#addCallback(BaseCallback)
     */
    public static class Callback extends BaseCallback<NiceSnackBar> {
        /**
         * Indicates that the Snackbar was dismissed via a swipe.
         */
        public static final int DISMISS_EVENT_SWIPE = BaseCallback.DISMISS_EVENT_SWIPE;
        /**
         * Indicates that the Snackbar was dismissed via an action click.
         */
        public static final int DISMISS_EVENT_ACTION = BaseCallback.DISMISS_EVENT_ACTION;
        /**
         * Indicates that the Snackbar was dismissed via a timeout.
         */
        public static final int DISMISS_EVENT_TIMEOUT = BaseCallback.DISMISS_EVENT_TIMEOUT;
        /**
         * Indicates that the Snackbar was dismissed via a call to {@link #dismiss()}.
         */
        public static final int DISMISS_EVENT_MANUAL = BaseCallback.DISMISS_EVENT_MANUAL;
        /**
         * Indicates that the Snackbar was dismissed from a new Snackbar being shown.
         */
        public static final int DISMISS_EVENT_CONSECUTIVE = BaseCallback.DISMISS_EVENT_CONSECUTIVE;

        @Override
        public void onShown(NiceSnackBar sb) {
            // Stub implementation to make API check happy.
        }

        @Override
        public void onDismissed(NiceSnackBar transientBottomBar, @DismissEvent int event) {
            // Stub implementation to make API check happy.
        }
    }

    @Nullable
    private BaseCallback<NiceSnackBar> mCallback;

    private NiceSnackBar(ViewGroup parent, View content, ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
    }

    /**
     * Make a Snackbar to display a message
     * <p>
     * <p>Snackbar will try and find a parent view to hold Snackbar's view from the value given
     * to {@code view}. Snackbar will walk up the view tree trying to find a suitable parent,
     * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
     * whichever comes first.
     * <p>
     * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows Snackbar to enable
     * certain features, such as swipe-to-dismiss and automatically moving of widgets like
     * {@link FloatingActionButton}.
     *
     * @param view     The view to find a parent from.
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
     *                 #LENGTH_LONG}
     */
    @NonNull
    public static NiceSnackBar make(@NonNull View view, @NonNull CharSequence text,
                                    @Duration int duration) {
        final ViewGroup parent = findSuitableParent(view);
        if (parent == null) {
            throw new IllegalArgumentException("No suitable parent found from the given view. "
                    + "Please provide a valid view.");
        }

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final SnackbarContentLayout content =
                (SnackbarContentLayout) inflater.inflate(
                        R.layout.design_layout_top_snackbar_include, parent, false);
        final NiceSnackBar snackBar = new NiceSnackBar(parent, content, content);
        snackBar.setText(text);
        snackBar.setDuration(duration);
        return snackBar;
    }

    /**
     * Make a Snackbar to display a message.
     * <p>
     * <p>Snackbar will try and find a parent view to hold Snackbar's view from the value given
     * to {@code view}. Snackbar will walk up the view tree trying to find a suitable parent,
     * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
     * whichever comes first.
     * <p>
     * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows Snackbar to enable
     * certain features, such as swipe-to-dismiss and automatically moving of widgets like
     * {@link FloatingActionButton}.
     *
     * @param view     The view to find a parent from.
     * @param resId    The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
     *                 #LENGTH_LONG}
     */
    @NonNull
    public static NiceSnackBar make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    /**
     * Update the text in this {@link NiceSnackBar}.
     *
     * @param message The new text for this {@link BaseTransientBottomBar}.
     */
    @NonNull
    public NiceSnackBar setText(@NonNull CharSequence message) {
        final SnackbarContentLayout contentLayout = (SnackbarContentLayout) mView.getChildAt(0);
        final TextView tv = contentLayout.getMessageView();
        tv.setText(message);
        return this;
    }

    /**
     * Update the text in this {@link NiceSnackBar}.
     *
     * @param resId The new text for this {@link BaseTransientBottomBar}.
     */
    @NonNull
    public NiceSnackBar setText(@StringRes int resId) {
        return setText(getContext().getText(resId));
    }

    /**
     * Set the action to be displayed in this {@link BaseTransientBottomBar}.
     *
     * @param resId    String resource to display for the action
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public NiceSnackBar setAction(@StringRes int resId, View.OnClickListener listener) {
        return setAction(getContext().getText(resId), listener);
    }

    /**
     * Set the action to be displayed in this {@link BaseTransientBottomBar}.
     *
     * @param text     Text to display for the action
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public NiceSnackBar setAction(CharSequence text, final View.OnClickListener listener) {
        final SnackbarContentLayout contentLayout = (SnackbarContentLayout) mView.getChildAt(0);
        final TextView tv = contentLayout.getActionView();

        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener(null);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);
                    // Now dismiss the Snackbar
                    dispatchDismiss(BaseCallback.DISMISS_EVENT_ACTION);
                }
            });
        }
        return this;
    }

    /**
     * Sets the text color of the action specified in
     * {@link #setAction(CharSequence, View.OnClickListener)}.
     */
    @NonNull
    public NiceSnackBar setActionTextColor(ColorStateList colors) {
        final SnackbarContentLayout contentLayout = (SnackbarContentLayout) mView.getChildAt(0);
        final TextView tv = contentLayout.getActionView();
        tv.setTextColor(colors);
        return this;
    }

    /**
     * Sets the text color of the action specified in
     * {@link #setAction(CharSequence, View.OnClickListener)}.
     */
    @NonNull
    public NiceSnackBar setActionTextColor(@ColorInt int color) {
        final SnackbarContentLayout contentLayout = (SnackbarContentLayout) mView.getChildAt(0);
        final TextView tv = contentLayout.getActionView();
        tv.setTextColor(color);
        return this;
    }

    /**
     * Set a callback to be called when this the visibility of this {@link NiceSnackBar}
     * changes. Note that this method is deprecated
     * and you should use {@link #addCallback(BaseCallback)} to add a callback and
     * {@link #removeCallback(BaseCallback)} to remove a registered callback.
     *
     * @param callback Callback to notify when transient bottom bar events occur.
     * @see Callback
     * @see #addCallback(BaseCallback)
     * @see #removeCallback(BaseCallback)
     * @deprecated Use {@link #addCallback(BaseCallback)}
     */
    @Deprecated
    @NonNull
    public NiceSnackBar setCallback(Callback callback) {
        // The logic in this method emulates what we had before support for multiple
        // registered callbacks.
        if (mCallback != null) {
            removeCallback(mCallback);
        }
        if (callback != null) {
            addCallback(callback);
        }
        // Update the deprecated field so that we can remove the passed callback the next
        // time we're called
        mCallback = callback;
        return this;
    }

    /**
     * @hide Note: this class is here to provide backwards-compatible way for apps written before
     * the existence of the base {@link BaseTransientBottomBar} class.
     */
    @RestrictTo(LIBRARY_GROUP)
    public static final class SnackBarLayout extends BaseTransientBottomBar.SnackbarBaseLayout {
        public SnackBarLayout(Context context) {
            super(context);
        }

        public SnackBarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            // Work around our backwards-compatible refactoring of Snackbar and inner content
            // being inflated against snackbar's parent (instead of against the snackbar itself).
            // Every child that is width=MATCH_PARENT is remeasured again and given the full width
            // minus the paddings.
            int childCount = getChildCount();
            int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),
                                    View.MeasureSpec.EXACTLY));
                }
            }
        }
    }
}

