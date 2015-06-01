package com.xiexin.ces.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.xiexin.ces.R;

/**
 * User: special Date: 13-12-10 Time: 下午10:44 Mail: specialcyci@gmail.com
 */
public class ResideMenu extends FrameLayout {

	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	private static final int PRESSED_MOVE_HORIZONTAL = 2;
	private static final int PRESSED_DOWN = 3;
	private static final int PRESSED_DONE = 4;
	private static final int PRESSED_MOVE_VERTICAL = 5;

	private ImageView imageViewShadow;
	private ImageView imageViewBackground;
	private FrameLayout layoutLeftMenu;
	// private LinearLayout layoutRightMenu;
	private FrameLayout scrollViewLeftMenu;
	private FrameLayout scrollViewRightMenu;
	private FrameLayout scrollViewMenu;
	// private LinearLayout contentInfoLl;
	/** the activity that view attach to */
	private Activity activity;
	/** the decorview of the activity */
	private ViewGroup viewDecor;
	/** the viewgroup of the activity */
	private TouchDisableView viewActivity;
	/** the flag of menu open status */
	private boolean isOpened;
	private float shadowAdjustScaleX;
	private float shadowAdjustScaleY;
	/** the view which don't want to intercept touch event */
	private List<View> ignoredViews;
	private List<ResideMenuItem> leftMenuItems;
	private List<ResideMenuItem> rightMenuItems;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private OnMenuListener menuListener;
	private float lastRawX;
	private boolean isInIgnoredView = false;
	private int scaleDirection = DIRECTION_LEFT;
	private int pressedState = PRESSED_DOWN;
	private List<Integer> disabledSwipeDirection = new ArrayList<Integer>();
	// valid scale factor is between 0.0f and 1.0f.
	private float mScaleValue = 0.5f;
	
	private LinearLayout mNewMenu;
	private TextView mUserNameTv;
	private TextView mUserDeptTv;
	private TextView mUserAccountTv;
	private ImageView mUserHeaderIv;
	private LinearLayout mSwitchAccountLl;
	private LinearLayout mLoginOutLl;

	// header
	private RelativeLayout mUserInfoRl;
	private LinearLayout mSettingLl;

	public ResideMenu(Context context) {
		super(context);
		initViews(context);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu, this);

		// contentInfoLl = (LinearLayout) findViewById(R.id.content_info_ll);
		scrollViewLeftMenu = (FrameLayout) findViewById(R.id.sv_left_menu);
		scrollViewRightMenu = (FrameLayout) findViewById(R.id.sv_right_menu);
		imageViewShadow = (ImageView) findViewById(R.id.iv_shadow);
		layoutLeftMenu = (FrameLayout) findViewById(R.id.layout_left_menu);
		// layoutRightMenu = (LinearLayout)findViewById( R.id.layout_right_menu
		// );
		imageViewBackground = (ImageView) findViewById(R.id.iv_background);

		mUserInfoRl = (RelativeLayout) findViewById(R.id.user_info_rl);
		mSettingLl = (LinearLayout) findViewById(R.id.setting_ll);
		
		mNewMenu= (LinearLayout) findViewById(R.id.menu);
		
	      mUserNameTv = (TextView) findViewById(R.id.user_name_tv);
	        mUserDeptTv = (TextView) findViewById(R.id.user_dept_tv);
	        mUserAccountTv = (TextView) findViewById(R.id.user_account_tv);
	        mUserHeaderIv = (ImageView) findViewById(R.id.user_head_img);
	        
	        mLoginOutLl = (LinearLayout) findViewById(R.id.login_out_ll);
	        
	        mSwitchAccountLl = (LinearLayout) findViewById(R.id.switch_accout_ll);
	}
	
	
	public LinearLayout getNewMenuView(){
	    return mNewMenu;
	}
	public TextView getUserNameTv(){
	    return mUserNameTv;
	}
	public TextView getUserDeptTv(){
	    return mUserDeptTv;
	}
	public TextView getUserAccountTv(){
	    return mUserAccountTv;
	}
	public ImageView getUserHeaderIv(){
	    return mUserHeaderIv;
	}
	
	   public LinearLayout getSwitchAccountLl(){
	        return mSwitchAccountLl;
	   }
       public LinearLayout getLoginOutLl(){
           return mLoginOutLl;
       }
	/**
	 * use the method to set up the activity which residemenu need to show;
	 * 
	 * @param activity
	 */
	public void attachToActivity(Activity activity) {
		initValue(activity);
		setShadowAdjustScaleXByOrientation();
		viewDecor.addView(this, 0);
//		setViewPadding();
	}
	
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        // Applies the content insets to the view's padding, consuming that content (modifying the insets to be 0),
        // and returning true. This behavior is off by default and can be enabled through setFitsSystemWindows(boolean)
        // in api14+ devices.
        this.setPadding(viewActivity.getPaddingLeft() + insets.left, viewActivity.getPaddingTop() + insets.top,
                viewActivity.getPaddingRight() + insets.right, viewActivity.getPaddingBottom() + insets.bottom);
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return true;
    }

	private void initValue(Activity activity) {
		this.activity = activity;
		leftMenuItems = new ArrayList<ResideMenuItem>();
		rightMenuItems = new ArrayList<ResideMenuItem>();
		ignoredViews = new ArrayList<View>();
		viewDecor = (ViewGroup) activity.getWindow().getDecorView();
		viewActivity = new TouchDisableView(this.activity);

		View mContent = viewDecor.getChildAt(0);
		viewDecor.removeViewAt(0);
		viewActivity.setContent(mContent);
		addView(viewActivity);

		ViewGroup parent = (ViewGroup) scrollViewLeftMenu.getParent();
		parent.removeView(scrollViewLeftMenu);
		parent.removeView(scrollViewRightMenu);
		
	}

	private void setShadowAdjustScaleXByOrientation() {
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			shadowAdjustScaleX = 0.034f;
			shadowAdjustScaleY = 0.12f;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			shadowAdjustScaleX = 0.06f;
			shadowAdjustScaleY = 0.07f;
		}
	}

	/**
	 * set the menu background picture;
	 * 
	 * @param imageResrouce
	 */
	public void setBackground(int imageResource) {
		imageViewBackground.setImageResource(imageResource);
	}

	/**
	 * the visiblity of shadow under the activity view;
	 * 
	 * @param isVisible
	 */
	public void setShadowVisible(boolean isVisible) {
		if (isVisible)
			imageViewShadow.setBackgroundResource(R.drawable.shadow);
		else
			imageViewShadow.setBackgroundResource(0);
	}

	/**
	 * add a single items to left menu;
	 * 
	 * @param menuItem
	 */
	@Deprecated
	public void addMenuItem(ResideMenuItem menuItem) {
		this.leftMenuItems.add(menuItem);
		layoutLeftMenu.addView(menuItem);
	}

	/**
	 * add a single items;
	 * 
	 * @param menuItem
	 * @param direction
	 */
	public void addMenuItem(ResideMenuItem menuItem, int direction) {
		if (direction == DIRECTION_LEFT) {
			this.leftMenuItems.add(menuItem);
			layoutLeftMenu.addView(menuItem);
		} else {
			this.rightMenuItems.add(menuItem);
			// layoutRightMenu.addView( menuItem );
		}
	}

	// add menu group
	public void addMenuItem(List<ResideMenuItem> menuItems, int direction) {

		for (ResideMenuItem menuItem : menuItems) {
			if (direction == DIRECTION_LEFT) {
				this.leftMenuItems.add(menuItem);
				layoutLeftMenu.addView(menuItem);
			} else {
				this.rightMenuItems.add(menuItem);
				// layoutRightMenu.addView( menuItem );
			}
		}

	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	// 画直线
	private View createLineView() {
		View view = new View(this.activity);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, dp2px(1));
		layoutParams.setMargins(0, dp2px(5), 0, dp2px(5));
		view.setBackgroundColor(this.activity.getResources().getColor(
				R.color.menu_line_color));
		view.setLayoutParams(layoutParams);
		return view;
	}

	/**
	 * set the menu items by array list to left menu;
	 * 
	 * @param menuItems
	 */
	@Deprecated
	public void setMenuItems(List<ResideMenuItem> menuItems) {
		this.leftMenuItems = menuItems;
		rebuildMenu();
	}

	/**
	 * set the menu items by array list;
	 * 
	 * @param menuItems
	 * @param direction
	 */
	public void setMenuItems(List<ResideMenuItem> menuItems, int direction) {
		if (direction == DIRECTION_LEFT)
			this.leftMenuItems = menuItems;
		else
			this.rightMenuItems = menuItems;
		rebuildMenu();
	}

	private void rebuildMenu() {
		layoutLeftMenu.removeAllViews();
		// layoutRightMenu.removeAllViews( );
		for (int i = 0; i < leftMenuItems.size(); i++)
			layoutLeftMenu.addView(leftMenuItems.get(i), i);
		// for( int i = 0 ; i < rightMenuItems.size( ) ; i++ )
		// layoutRightMenu.addView( rightMenuItems.get( i ) , i );
	}

	/**
	 * get the left menu items;
	 * 
	 * @return
	 */
	@Deprecated
	public List<ResideMenuItem> getMenuItems() {
		return leftMenuItems;
	}

	/**
	 * get the menu items;
	 * 
	 * @return
	 */
	public List<ResideMenuItem> getMenuItems(int direction) {
		if (direction == DIRECTION_LEFT)
			return leftMenuItems;
		else
			return rightMenuItems;
	}

	/**
	 * if you need to do something on the action of closing or opening menu, set
	 * the listener here.
	 * 
	 * @return
	 */
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
	}

	public OnMenuListener getMenuListener() {
		return menuListener;
	}

	/**
	 * we need the call the method before the menu show, because the padding of
	 * activity can't get at the moment of onCreateView();
	 */
	private void setViewPadding() {
		this.setPadding(viewActivity.getPaddingLeft(),
				viewActivity.getPaddingTop(), viewActivity.getPaddingRight(),
				viewActivity.getPaddingBottom());
	}

	/**
	 * show the reside menu;
	 */
	public void openMenu(int direction) {

		setScaleDirection(direction);

		isOpened = true;
		AnimatorSet scaleDown_activity = buildScaleDownAnimation(viewActivity,
				mScaleValue, mScaleValue);
		AnimatorSet scaleDown_shadow = buildScaleDownAnimation(imageViewShadow,
				mScaleValue + shadowAdjustScaleX, mScaleValue
						+ shadowAdjustScaleY);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 1.0f);
		AnimatorSet alpha_userinfo = buildMenuAnimation(mUserInfoRl, 1.0f);
		AnimatorSet alpha_loginout = buildMenuAnimation(mSettingLl, 1.0f);

		scaleDown_shadow.addListener(animationListener);
		scaleDown_activity.playTogether(scaleDown_shadow);
		scaleDown_activity.playTogether(alpha_menu);
		scaleDown_activity.playTogether(alpha_userinfo);
		scaleDown_activity.playTogether(alpha_loginout);
		scaleDown_activity.start();
	}

	/**
	 * close the reslide menu;
	 */
	public void closeMenu() {

		isOpened = false;
		AnimatorSet scaleUp_activity = buildScaleUpAnimation(viewActivity,
				1.0f, 1.0f);
		AnimatorSet scaleUp_shadow = buildScaleUpAnimation(imageViewShadow,
				1.0f, 1.0f);
		AnimatorSet alpha_menu = buildMenuAnimation(scrollViewMenu, 0.0f);
		AnimatorSet alpha_userinfo = buildMenuAnimation(mUserInfoRl, 0.0f);
		AnimatorSet alpha_loginout = buildMenuAnimation(mSettingLl, 0.0f);
		scaleUp_activity.addListener(animationListener);
		 scaleUp_activity.playTogether( scaleUp_shadow );
		scaleUp_activity.playTogether(alpha_menu);
		scaleUp_activity.playTogether(alpha_userinfo);
		scaleUp_activity.playTogether(alpha_loginout);
		scaleUp_activity.start();
	}

	@Deprecated
	public void setDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	public void setSwipeDirectionDisable(int direction) {
		disabledSwipeDirection.add(direction);
	}

	private boolean isInDisableDirection(int direction) {
		return disabledSwipeDirection.contains(direction);
	}

	private void setScaleDirection(int direction) {

		int screenWidth = getScreenWidth();
		float pivotX;
		float pivotY = getScreenHeight() * 0.5f;

		if (direction == DIRECTION_LEFT) {
			scrollViewMenu = scrollViewLeftMenu;
			pivotX = screenWidth * 1.5f;
		} else {
			scrollViewMenu = scrollViewRightMenu;
			pivotX = screenWidth * -0.5f;
		}

		ViewHelper.setPivotX(viewActivity, pivotX);
		ViewHelper.setPivotY(viewActivity, pivotY);
		ViewHelper.setPivotX(imageViewShadow, pivotX);
		ViewHelper.setPivotY(imageViewShadow, pivotY);
		scaleDirection = direction;
	}

	/**
	 * return the flag of menu status;
	 * 
	 * @return
	 */
	public boolean isOpened() {
		return isOpened;
	}

	private OnClickListener viewActivityOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (isOpened())
				closeMenu();
		}
	};

	private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			if (isOpened()) {
				showUserInfoView(mUserInfoRl);

				showScrollViewMenu(scrollViewMenu);

				showSettingLlView(mSettingLl);

				if (menuListener != null)
					menuListener.openMenu();
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// reset the view;
			if (isOpened()) {
				viewActivity.setTouchDisable(true);
				viewActivity.setOnClickListener(viewActivityOnClickListener);
			} else {
				viewActivity.setTouchDisable(false);
				viewActivity.setOnClickListener(null);

				hideUserInfoView(mUserInfoRl);

				hideScrollViewMenu(scrollViewLeftMenu);
				hideScrollViewMenu(scrollViewRightMenu);

				hideLoginOutView(mSettingLl);

				if (menuListener != null)
					menuListener.closeMenu();
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	};

	/**
	 * a helper method to build scale down animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleDownAnimation(View target,
			float targetScaleX, float targetScaleY) {

		AnimatorSet scaleDown = new AnimatorSet();
		scaleDown.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleDown.setInterpolator(AnimationUtils.loadInterpolator(activity,
				android.R.anim.decelerate_interpolator));
		scaleDown.setDuration(250);
		return scaleDown;
	}

	/**
	 * a helper method to build scale up animation;
	 * 
	 * @param target
	 * @param targetScaleX
	 * @param targetScaleY
	 * @return
	 */
	private AnimatorSet buildScaleUpAnimation(View target, float targetScaleX,
			float targetScaleY) {

		AnimatorSet scaleUp = new AnimatorSet();
		scaleUp.playTogether(
				ObjectAnimator.ofFloat(target, "scaleX", targetScaleX),
				ObjectAnimator.ofFloat(target, "scaleY", targetScaleY));

		scaleUp.setDuration(250);
		return scaleUp;
	}

	private AnimatorSet buildMenuAnimation(View target, float alpha) {

		AnimatorSet alphaAnimation = new AnimatorSet();
		alphaAnimation.playTogether(ObjectAnimator.ofFloat(target, "alpha",
				alpha));

		alphaAnimation.setDuration(250);
		return alphaAnimation;
	}

	/**
	 * if there ware some view you don't want reside menu to intercept their
	 * touch event,you can use the method to set.
	 * 
	 * @param v
	 */
	public void addIgnoredView(View v) {
		ignoredViews.add(v);
	}

	/**
	 * remove the view from ignored view list;
	 * 
	 * @param v
	 */
	public void removeIgnoredView(View v) {
		ignoredViews.remove(v);
	}

	/**
	 * clear the ignored view list;
	 */
	public void clearIgnoredViewList() {
		ignoredViews.clear();
	}

	/**
	 * if the motion evnent was relative to the view which in ignored view
	 * list,return true;
	 * 
	 * @param ev
	 * @return
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : ignoredViews) {
			v.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY()))
				return true;
		}
		return false;
	}

	private void setScaleDirectionByRawX(float currentRawX) {
		if (currentRawX < lastRawX)
			setScaleDirection(DIRECTION_RIGHT);
		else
			setScaleDirection(DIRECTION_LEFT);
	}

	private float getTargetScale(float currentRawX) {
		float scaleFloatX = ((currentRawX - lastRawX) / getScreenWidth()) * 0.75f;
		scaleFloatX = scaleDirection == DIRECTION_RIGHT ? -scaleFloatX
				: scaleFloatX;

		float targetScale = ViewHelper.getScaleX(viewActivity) - scaleFloatX;
		targetScale = targetScale > 1.0f ? 1.0f : targetScale;
		targetScale = targetScale < 0.5f ? 0.5f : targetScale;
		return targetScale;
	}

	private float lastActionDownX, lastActionDownY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		float currentActivityScaleX = ViewHelper.getScaleX(viewActivity);
		if (currentActivityScaleX == 1.0f)
			setScaleDirectionByRawX(ev.getRawX());

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastActionDownX = ev.getX();
			lastActionDownY = ev.getY();
			isInIgnoredView = isInIgnoredView(ev) && !isOpened();
			pressedState = PRESSED_DOWN;
			break;

		case MotionEvent.ACTION_MOVE:
			if (isInIgnoredView || isInDisableDirection(scaleDirection))
				break;

			if (pressedState != PRESSED_DOWN
					&& pressedState != PRESSED_MOVE_HORIZONTAL)
				break;

			int xOffset = (int) (ev.getX() - lastActionDownX);
			int yOffset = (int) (ev.getY() - lastActionDownY);

			if (pressedState == PRESSED_DOWN) {
				if (yOffset > 25 || yOffset < -25) {
					pressedState = PRESSED_MOVE_VERTICAL;
					break;
				}
				if (xOffset < -50 || xOffset > 50) {
					pressedState = PRESSED_MOVE_HORIZONTAL;
					ev.setAction(MotionEvent.ACTION_CANCEL);
				}
			} else if (pressedState == PRESSED_MOVE_HORIZONTAL) {
				if (currentActivityScaleX < 0.95) {
					showScrollViewMenu(scrollViewMenu);
					showUserInfoView(mUserInfoRl);
					showSettingLlView(mSettingLl);
				}

				float targetScale = getTargetScale(ev.getRawX());
				ViewHelper.setScaleX(viewActivity, targetScale);
				ViewHelper.setScaleY(viewActivity, targetScale);
				ViewHelper.setScaleX(imageViewShadow, targetScale
						+ shadowAdjustScaleX);
				ViewHelper.setScaleY(imageViewShadow, targetScale
						+ shadowAdjustScaleY);
				ViewHelper.setAlpha(scrollViewMenu, (1 - targetScale) * 2.0f);
				ViewHelper.setAlpha(mSettingLl, (1 - targetScale) * 2.0f);
				ViewHelper.setAlpha(mUserInfoRl, (1 - targetScale) * 2.0f);

				lastRawX = ev.getRawX();
				return true;
			}

			break;

		case MotionEvent.ACTION_UP:

			if (isInIgnoredView)
				break;
			if (pressedState != PRESSED_MOVE_HORIZONTAL)
				break;

			pressedState = PRESSED_DONE;
			if (isOpened()) {
				if (currentActivityScaleX > 0.56f)
					closeMenu();
				else
					openMenu(scaleDirection);
			} else {
				if (currentActivityScaleX < 0.94f) {
					openMenu(scaleDirection);
				} else {
					closeMenu();
				}
			}

			break;

		}
		lastRawX = ev.getRawX();
		return super.dispatchTouchEvent(ev);
	}

	public int getScreenHeight() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public int getScreenWidth() {
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	public void setScaleValue(float scaleValue) {
		this.mScaleValue = scaleValue;
	}

	public interface OnMenuListener {

		/**
		 * the method will call on the finished time of opening menu's
		 * animation.
		 */
		public void openMenu();

		/**
		 * the method will call on the finished time of closing menu's animation
		 * .
		 */
		public void closeMenu();
	}

	private void showScrollViewMenu(FrameLayout scrollViewMenu) {
		if (scrollViewMenu != null && scrollViewMenu.getParent() == null) {
			addView(scrollViewMenu);
		}
	}

	private void hideScrollViewMenu(FrameLayout scrollViewMenu) {
		if (scrollViewMenu != null && scrollViewMenu.getParent() != null) {
			removeView(scrollViewMenu);
		}
	}

	private void showUserInfoView(RelativeLayout userInfoRl) {
		if (userInfoRl != null && userInfoRl.getParent() == null) {
			addView(userInfoRl);
		}
	}

	private void hideUserInfoView(RelativeLayout userInfoRl) {
		if (userInfoRl != null && userInfoRl.getParent() != null) {
			removeView(userInfoRl);
		}
	}

	private void showSettingLlView(LinearLayout settingLl) {
		if (settingLl != null && settingLl.getParent() == null) {
			addView(settingLl);
		}
	}

	private void hideLoginOutView(LinearLayout settingLl) {
		if (settingLl != null && settingLl.getParent() != null) {
			removeView(settingLl);
		}
	}
}
