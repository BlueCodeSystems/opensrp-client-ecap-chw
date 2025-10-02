Compiled from "BaseRegisterFragment.java"
public abstract class org.smartregister.view.fragment.BaseRegisterFragment extends org.smartregister.cursoradapter.RecyclerViewFragment implements org.smartregister.view.contract.BaseRegisterFragmentContract$View,org.smartregister.receiver.SyncStatusBroadcastReceiver$SyncStatusListener {
  public static java.lang.String TOOLBAR_TITLE;

  protected org.smartregister.view.fragment.BaseRegisterFragment$RegisterActionHandler registerActionHandler;

  protected org.smartregister.view.contract.BaseRegisterFragmentContract$Presenter presenter;

  protected android.view.View rootView;

  protected android.widget.TextView headerTextDisplay;

  protected android.widget.TextView filterStatus;

  protected android.widget.RelativeLayout filterRelativeLayout;

  protected android.view.View$OnKeyListener hideKeyboard;

  protected android.widget.ImageView qrCodeScanImageView;

  protected android.widget.ProgressBar syncProgressBar;

  protected android.widget.ImageView syncButton;

  protected boolean globalQrSearch;

  protected final android.text.TextWatcher textWatcher;

  public org.smartregister.view.fragment.BaseRegisterFragment();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method org/smartregister/cursoradapter/RecyclerViewFragment."<init>":()V
       4: aload_0
       5: new           #7                  // class org/smartregister/view/fragment/BaseRegisterFragment$RegisterActionHandler
       8: dup
       9: aload_0
      10: aconst_null
      11: invokespecial #9                  // Method org/smartregister/view/fragment/BaseRegisterFragment$RegisterActionHandler."<init>":(Lorg/smartregister/view/fragment/BaseRegisterFragment;Lorg/smartregister/view/fragment/BaseRegisterFragment$1;)V
      14: putfield      #12                 // Field registerActionHandler:Lorg/smartregister/view/fragment/BaseRegisterFragment$RegisterActionHandler;
      17: aload_0
      18: new           #18                 // class org/smartregister/view/fragment/BaseRegisterFragment$1
      21: dup
      22: aload_0
      23: invokespecial #20                 // Method org/smartregister/view/fragment/BaseRegisterFragment$1."<init>":(Lorg/smartregister/view/fragment/BaseRegisterFragment;)V
      26: putfield      #23                 // Field hideKeyboard:Landroid/view/View$OnKeyListener;
      29: aload_0
      30: iconst_0
      31: putfield      #27                 // Field globalQrSearch:Z
      34: aload_0
      35: new           #31                 // class org/smartregister/view/fragment/BaseRegisterFragment$2
      38: dup
      39: aload_0
      40: invokespecial #33                 // Method org/smartregister/view/fragment/BaseRegisterFragment$2."<init>":(Lorg/smartregister/view/fragment/BaseRegisterFragment;)V
      43: putfield      #34                 // Field textWatcher:Landroid/text/TextWatcher;
      46: return

  protected org.smartregister.view.activity.SecuredNativeSmartRegisterActivity$DefaultOptionsProvider getDefaultOptionsProvider();
    Code:
       0: aconst_null
       1: areturn

  protected org.smartregister.view.activity.SecuredNativeSmartRegisterActivity$NavBarOptionsProvider getNavBarOptionsProvider();
    Code:
       0: new           #38                 // class org/smartregister/view/fragment/BaseRegisterFragment$3
       3: dup
       4: aload_0
       5: invokespecial #40                 // Method org/smartregister/view/fragment/BaseRegisterFragment$3."<init>":(Lorg/smartregister/view/fragment/BaseRegisterFragment;)V
       8: areturn

  public android.view.View onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle);
    Code:
       0: aload_1
       1: aload_0
       2: invokevirtual #41                 // Method getLayout:()I
       5: aload_2
       6: iconst_0
       7: invokevirtual #45                 // Method android/view/LayoutInflater.inflate:(ILandroid/view/ViewGroup;Z)Landroid/view/View;
      10: astore        4
      12: aload_0
      13: aload         4
      15: putfield      #51                 // Field rootView:Landroid/view/View;
      18: aload_0
      19: invokevirtual #55                 // Method setUpActionBar:()V
      22: aload_0
      23: aload         4
      25: invokevirtual #58                 // Method setupViews:(Landroid/view/View;)V
      28: aload         4
      30: areturn

  protected void setUpActionBar();
    Code:
       0: aload_0
       1: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
       4: instanceof    #66                 // class androidx/appcompat/app/AppCompatActivity
       7: ifeq          88
      10: aload_0
      11: getfield      #51                 // Field rootView:Landroid/view/View;
      14: getstatic     #68                 // Field org/smartregister/R$id.register_toolbar:I
      17: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
      20: checkcast     #80                 // class androidx/appcompat/widget/Toolbar
      23: astore_1
      24: aload_0
      25: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      28: checkcast     #66                 // class androidx/appcompat/app/AppCompatActivity
      31: astore_2
      32: aload_2
      33: aload_1
      34: invokevirtual #82                 // Method androidx/appcompat/app/AppCompatActivity.setSupportActionBar:(Landroidx/appcompat/widget/Toolbar;)V
      37: aload_2
      38: invokevirtual #86                 // Method androidx/appcompat/app/AppCompatActivity.getSupportActionBar:()Landroidx/appcompat/app/ActionBar;
      41: aload_2
      42: invokevirtual #90                 // Method androidx/appcompat/app/AppCompatActivity.getIntent:()Landroid/content/Intent;
      45: getstatic     #94                 // Field TOOLBAR_TITLE:Ljava/lang/String;
      48: invokevirtual #98                 // Method android/content/Intent.getStringExtra:(Ljava/lang/String;)Ljava/lang/String;
      51: invokevirtual #104                // Method androidx/appcompat/app/ActionBar.setTitle:(Ljava/lang/CharSequence;)V
      54: aload_2
      55: invokevirtual #86                 // Method androidx/appcompat/app/AppCompatActivity.getSupportActionBar:()Landroidx/appcompat/app/ActionBar;
      58: iconst_0
      59: invokevirtual #110                // Method androidx/appcompat/app/ActionBar.setDisplayHomeAsUpEnabled:(Z)V
      62: aload_2
      63: invokevirtual #86                 // Method androidx/appcompat/app/AppCompatActivity.getSupportActionBar:()Landroidx/appcompat/app/ActionBar;
      66: getstatic     #114                // Field org/smartregister/R$drawable.round_white_background:I
      69: invokevirtual #119                // Method androidx/appcompat/app/ActionBar.setLogo:(I)V
      72: aload_2
      73: invokevirtual #86                 // Method androidx/appcompat/app/AppCompatActivity.getSupportActionBar:()Landroidx/appcompat/app/ActionBar;
      76: iconst_0
      77: invokevirtual #123                // Method androidx/appcompat/app/ActionBar.setDisplayUseLogoEnabled:(Z)V
      80: aload_2
      81: invokevirtual #86                 // Method androidx/appcompat/app/AppCompatActivity.getSupportActionBar:()Landroidx/appcompat/app/ActionBar;
      84: iconst_0
      85: invokevirtual #126                // Method androidx/appcompat/app/ActionBar.setDisplayShowTitleEnabled:(Z)V
      88: return

  protected int getLayout();
    Code:
       0: getstatic     #129                // Field org/smartregister/R$layout.fragment_base_register:I
       3: ireturn

  protected abstract void initializePresenter();

  protected void updateSearchView();
    Code:
       0: aload_0
       1: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
       4: ifnull        40
       7: aload_0
       8: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
      11: aload_0
      12: getfield      #34                 // Field textWatcher:Landroid/text/TextWatcher;
      15: invokevirtual #138                // Method android/widget/EditText.removeTextChangedListener:(Landroid/text/TextWatcher;)V
      18: aload_0
      19: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
      22: aload_0
      23: getfield      #34                 // Field textWatcher:Landroid/text/TextWatcher;
      26: invokevirtual #144                // Method android/widget/EditText.addTextChangedListener:(Landroid/text/TextWatcher;)V
      29: aload_0
      30: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
      33: aload_0
      34: getfield      #23                 // Field hideKeyboard:Landroid/view/View$OnKeyListener;
      37: invokevirtual #147                // Method android/widget/EditText.setOnKeyListener:(Landroid/view/View$OnKeyListener;)V
      40: return

  public void updateSearchBarHint(java.lang.String);
    Code:
       0: aload_0
       1: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
       4: ifnull        15
       7: aload_0
       8: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
      11: aload_1
      12: invokevirtual #151                // Method android/widget/EditText.setHint:(Ljava/lang/CharSequence;)V
      15: return

  public void setSearchTerm(java.lang.String);
    Code:
       0: aload_0
       1: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
       4: ifnull        15
       7: aload_0
       8: invokevirtual #134                // Method getSearchView:()Landroid/widget/EditText;
      11: aload_1
      12: invokevirtual #154                // Method android/widget/EditText.setText:(Ljava/lang/CharSequence;)V
      15: return

  public void onQRCodeSucessfullyScanned(java.lang.String);
    Code:
       0: ldc           #157                // String QR code: %s
       2: iconst_1
       3: anewarray     #159                // class java/lang/Object
       6: dup
       7: iconst_0
       8: aload_1
       9: aastore
      10: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
      13: aload_1
      14: invokestatic  #167                // Method org/apache/commons/lang3/StringUtils.isNotBlank:(Ljava/lang/CharSequence;)Z
      17: ifeq          44
      20: aload_0
      21: aload_1
      22: ldc           #173                // String -
      24: ldc           #175                // String
      26: invokevirtual #177                // Method java/lang/String.replace:(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
      29: ldc           #175                // String
      31: aload_0
      32: invokevirtual #183                // Method getMainCondition:()Ljava/lang/String;
      35: iconst_1
      36: invokevirtual #187                // Method filter:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V
      39: aload_0
      40: aload_1
      41: invokevirtual #191                // Method setUniqueID:(Ljava/lang/String;)V
      44: return

  public abstract void setUniqueID(java.lang.String);

  public abstract void setAdvancedSearchFormData(java.util.HashMap<java.lang.String, java.lang.String>);

  public void setupViews(android.view.View);
    Code:
       0: aload_0
       1: aload_1
       2: invokespecial #195                // Method org/smartregister/cursoradapter/RecyclerViewFragment.setupViews:(Landroid/view/View;)V
       5: aload_0
       6: getfield      #196                // Field clientsView:Landroidx/recyclerview/widget/RecyclerView;
       9: iconst_0
      10: invokevirtual #200                // Method androidx/recyclerview/widget/RecyclerView.setVisibility:(I)V
      13: aload_0
      14: getfield      #205                // Field clientsProgressView:Landroid/widget/ProgressBar;
      17: iconst_4
      18: invokevirtual #209                // Method android/widget/ProgressBar.setVisibility:(I)V
      21: aload_0
      22: getfield      #212                // Field presenter:Lorg/smartregister/view/contract/BaseRegisterFragmentContract$Presenter;
      25: invokeinterface #216,  1          // InterfaceMethod org/smartregister/view/contract/BaseRegisterFragmentContract$Presenter.processViewConfigurations:()V
      30: aload_0
      31: getfield      #212                // Field presenter:Lorg/smartregister/view/contract/BaseRegisterFragmentContract$Presenter;
      34: aload_0
      35: invokevirtual #183                // Method getMainCondition:()Ljava/lang/String;
      38: invokeinterface #221,  2          // InterfaceMethod org/smartregister/view/contract/BaseRegisterFragmentContract$Presenter.initializeQueries:(Ljava/lang/String;)V
      43: aload_0
      44: invokevirtual #224                // Method updateSearchView:()V
      47: aload_0
      48: aconst_null
      49: invokevirtual #227                // Method setServiceModeViewDrawableRight:(Landroid/graphics/drawable/Drawable;)V
      52: aload_0
      53: aload_1
      54: invokevirtual #231                // Method attachQrCode:(Landroid/view/View;)V
      57: aload_0
      58: aload_1
      59: invokevirtual #234                // Method attachSyncButton:(Landroid/view/View;)V
      62: aload_0
      63: aload_1
      64: invokevirtual #237                // Method attachTopLeftLayout:(Landroid/view/View;)V
      67: aload_0
      68: aload_1
      69: invokevirtual #240                // Method attachProgressBar:(Landroid/view/View;)V
      72: aload_0
      73: aload_1
      74: getstatic     #243                // Field org/smartregister/R$id.header_text_display:I
      77: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
      80: checkcast     #246                // class android/widget/TextView
      83: putfield      #248                // Field headerTextDisplay:Landroid/widget/TextView;
      86: aload_0
      87: aload_1
      88: getstatic     #252                // Field org/smartregister/R$id.filter_status:I
      91: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
      94: checkcast     #246                // class android/widget/TextView
      97: putfield      #255                // Field filterStatus:Landroid/widget/TextView;
     100: aload_0
     101: aload_1
     102: getstatic     #258                // Field org/smartregister/R$id.filter_display_view:I
     105: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
     108: checkcast     #261                // class android/widget/RelativeLayout
     111: putfield      #263                // Field filterRelativeLayout:Landroid/widget/RelativeLayout;
     114: return

  protected void attachTopLeftLayout(android.view.View);
    Code:
       0: aload_1
       1: getstatic     #267                // Field org/smartregister/R$id.top_left_layout:I
       4: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
       7: astore_2
       8: aload_2
       9: ifnull        22
      12: aload_2
      13: aload_0
      14: invokedynamic #270,  0            // InvokeDynamic #0:onClick:(Lorg/smartregister/view/fragment/BaseRegisterFragment;)Landroid/view/View$OnClickListener;
      19: invokevirtual #274                // Method android/view/View.setOnClickListener:(Landroid/view/View$OnClickListener;)V
      22: return

  protected void attachProgressBar(android.view.View);
    Code:
       0: aload_0
       1: aload_1
       2: getstatic     #278                // Field org/smartregister/R$id.sync_progress_bar:I
       5: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
       8: checkcast     #210                // class android/widget/ProgressBar
      11: putfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      14: aload_0
      15: getfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      18: ifnull        37
      21: new           #284                // class com/github/ybq/android/spinkit/style/FadingCircle
      24: dup
      25: invokespecial #286                // Method com/github/ybq/android/spinkit/style/FadingCircle."<init>":()V
      28: astore_2
      29: aload_0
      30: getfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      33: aload_2
      34: invokevirtual #287                // Method android/widget/ProgressBar.setIndeterminateDrawable:(Landroid/graphics/drawable/Drawable;)V
      37: return

  protected void attachSyncButton(android.view.View);
    Code:
       0: aload_0
       1: aload_1
       2: getstatic     #290                // Field org/smartregister/R$id.sync_refresh:I
       5: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
       8: checkcast     #293                // class android/widget/ImageView
      11: putfield      #295                // Field syncButton:Landroid/widget/ImageView;
      14: aload_0
      15: getfield      #295                // Field syncButton:Landroid/widget/ImageView;
      18: ifnull        33
      21: aload_0
      22: getfield      #295                // Field syncButton:Landroid/widget/ImageView;
      25: invokedynamic #299,  0            // InvokeDynamic #1:onClick:()Landroid/view/View$OnClickListener;
      30: invokevirtual #302                // Method android/widget/ImageView.setOnClickListener:(Landroid/view/View$OnClickListener;)V
      33: return

  protected void attachQrCode(android.view.View);
    Code:
       0: aload_0
       1: aload_1
       2: getstatic     #303                // Field org/smartregister/R$id.scanQrCode:I
       5: invokevirtual #74                 // Method android/view/View.findViewById:(I)Landroid/view/View;
       8: checkcast     #293                // class android/widget/ImageView
      11: putfield      #306                // Field qrCodeScanImageView:Landroid/widget/ImageView;
      14: aload_0
      15: getfield      #306                // Field qrCodeScanImageView:Landroid/widget/ImageView;
      18: ifnull        34
      21: aload_0
      22: getfield      #306                // Field qrCodeScanImageView:Landroid/widget/ImageView;
      25: aload_0
      26: invokedynamic #309,  0            // InvokeDynamic #2:onClick:(Lorg/smartregister/view/fragment/BaseRegisterFragment;)Landroid/view/View$OnClickListener;
      31: invokevirtual #302                // Method android/widget/ImageView.setOnClickListener:(Landroid/view/View$OnClickListener;)V
      34: return

  protected void onResumption();
    Code:
       0: aload_0
       1: invokespecial #310                // Method org/smartregister/cursoradapter/RecyclerViewFragment.onResumption:()V
       4: aload_0
       5: invokevirtual #313                // Method renderView:()V
       8: return

  protected void renderView();
    Code:
       0: aload_0
       1: invokevirtual #316                // Method getDefaultOptionsProvider:()Lorg/smartregister/view/activity/SecuredNativeSmartRegisterActivity$DefaultOptionsProvider;
       4: pop
       5: aload_0
       6: invokevirtual #320                // Method isPausedOrRefreshList:()Z
       9: ifeq          25
      12: aload_0
      13: getfield      #212                // Field presenter:Lorg/smartregister/view/contract/BaseRegisterFragmentContract$Presenter;
      16: aload_0
      17: invokevirtual #183                // Method getMainCondition:()Ljava/lang/String;
      20: invokeinterface #221,  2          // InterfaceMethod org/smartregister/view/contract/BaseRegisterFragmentContract$Presenter.initializeQueries:(Ljava/lang/String;)V
      25: aload_0
      26: invokevirtual #224                // Method updateSearchView:()V
      29: aload_0
      30: getfield      #212                // Field presenter:Lorg/smartregister/view/contract/BaseRegisterFragmentContract$Presenter;
      33: invokeinterface #216,  1          // InterfaceMethod org/smartregister/view/contract/BaseRegisterFragmentContract$Presenter.processViewConfigurations:()V
      38: aload_0
      39: invokevirtual #324                // Method refreshSyncProgressSpinner:()V
      42: aload_0
      43: invokevirtual #327                // Method setTotalPatients:()V
      46: return

  public void setTotalPatients();
    Code:
       0: aload_0
       1: getfield      #248                // Field headerTextDisplay:Landroid/widget/TextView;
       4: ifnull        97
       7: aload_0
       8: getfield      #248                // Field headerTextDisplay:Landroid/widget/TextView;
      11: aload_0
      12: getfield      #330                // Field clientAdapter:Lorg/smartregister/cursoradapter/RecyclerViewPaginatedAdapter;
      15: invokevirtual #334                // Method org/smartregister/cursoradapter/RecyclerViewPaginatedAdapter.getTotalcount:()I
      18: iconst_1
      19: if_icmple     55
      22: aload_0
      23: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      26: getstatic     #339                // Field org/smartregister/R$string.clients:I
      29: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
      32: iconst_1
      33: anewarray     #159                // class java/lang/Object
      36: dup
      37: iconst_0
      38: aload_0
      39: getfield      #330                // Field clientAdapter:Lorg/smartregister/cursoradapter/RecyclerViewPaginatedAdapter;
      42: invokevirtual #334                // Method org/smartregister/cursoradapter/RecyclerViewPaginatedAdapter.getTotalcount:()I
      45: invokestatic  #350                // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
      48: aastore
      49: invokestatic  #356                // Method java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      52: goto          85
      55: aload_0
      56: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      59: getstatic     #360                // Field org/smartregister/R$string.client:I
      62: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
      65: iconst_1
      66: anewarray     #159                // class java/lang/Object
      69: dup
      70: iconst_0
      71: aload_0
      72: getfield      #330                // Field clientAdapter:Lorg/smartregister/cursoradapter/RecyclerViewPaginatedAdapter;
      75: invokevirtual #334                // Method org/smartregister/cursoradapter/RecyclerViewPaginatedAdapter.getTotalcount:()I
      78: invokestatic  #350                // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
      81: aastore
      82: invokestatic  #356                // Method java/lang/String.format:(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      85: invokevirtual #363                // Method android/widget/TextView.setText:(Ljava/lang/CharSequence;)V
      88: aload_0
      89: getfield      #263                // Field filterRelativeLayout:Landroid/widget/RelativeLayout;
      92: bipush        8
      94: invokevirtual #364                // Method android/widget/RelativeLayout.setVisibility:(I)V
      97: return

  public void initializeQueryParams(java.lang.String, java.lang.String, java.lang.String);
    Code:
       0: aload_0
       1: aload_1
       2: putfield      #365                // Field tablename:Ljava/lang/String;
       5: aload_0
       6: aload_0
       7: invokevirtual #183                // Method getMainCondition:()Ljava/lang/String;
      10: putfield      #368                // Field mainCondition:Ljava/lang/String;
      13: aload_0
      14: aload_2
      15: putfield      #371                // Field countSelect:Ljava/lang/String;
      18: aload_0
      19: aload_3
      20: putfield      #374                // Field mainSelect:Ljava/lang/String;
      23: aload_0
      24: aload_0
      25: invokevirtual #377                // Method getDefaultSortQuery:()Ljava/lang/String;
      28: putfield      #380                // Field Sortqueries:Ljava/lang/String;
      31: return

  protected abstract java.lang.String getMainCondition();

  protected abstract java.lang.String getDefaultSortQuery();

  public void filter(java.lang.String, java.lang.String, java.lang.String, boolean);
    Code:
       0: aload_0
       1: invokevirtual #383                // Method getSearchCancelView:()Landroid/view/View;
       4: aload_1
       5: invokestatic  #387                // Method org/apache/commons/lang3/StringUtils.isEmpty:(Ljava/lang/CharSequence;)Z
       8: ifeq          15
      11: iconst_4
      12: goto          16
      15: iconst_0
      16: invokevirtual #390                // Method android/view/View.setVisibility:(I)V
      19: aload_1
      20: invokestatic  #387                // Method org/apache/commons/lang3/StringUtils.isEmpty:(Ljava/lang/CharSequence;)Z
      23: ifeq          33
      26: aload_0
      27: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      30: invokestatic  #391                // Method org/smartregister/util/Utils.hideKeyboard:(Landroid/app/Activity;)V
      33: aload_0
      34: aload_1
      35: putfield      #396                // Field filters:Ljava/lang/String;
      38: aload_0
      39: aload_2
      40: putfield      #399                // Field joinTable:Ljava/lang/String;
      43: aload_0
      44: aload_3
      45: putfield      #368                // Field mainCondition:Ljava/lang/String;
      48: aload_0
      49: invokevirtual #402                // Method countExecute:()V
      52: iload         4
      54: ifeq          98
      57: aload_1
      58: invokestatic  #167                // Method org/apache/commons/lang3/StringUtils.isNotBlank:(Ljava/lang/CharSequence;)Z
      61: ifeq          98
      64: aload_0
      65: getfield      #330                // Field clientAdapter:Lorg/smartregister/cursoradapter/RecyclerViewPaginatedAdapter;
      68: invokevirtual #334                // Method org/smartregister/cursoradapter/RecyclerViewPaginatedAdapter.getTotalcount:()I
      71: ifne          98
      74: invokestatic  #405                // Method org/smartregister/util/NetworkUtils.isNetworkAvailable:()Z
      77: ifeq          98
      80: aload_0
      81: iconst_1
      82: putfield      #27                 // Field globalQrSearch:Z
      85: aload_0
      86: getfield      #212                // Field presenter:Lorg/smartregister/view/contract/BaseRegisterFragmentContract$Presenter;
      89: aload_1
      90: invokeinterface #410,  2          // InterfaceMethod org/smartregister/view/contract/BaseRegisterFragmentContract$Presenter.searchGlobally:(Ljava/lang/String;)V
      95: goto          102
      98: aload_0
      99: invokevirtual #413                // Method filterandSortExecute:()V
     102: aload_0
     103: invokevirtual #327                // Method setTotalPatients:()V
     106: return

  public void updateFilterAndFilterStatus(java.lang.String, java.lang.String);
    Code:
       0: aload_0
       1: getfield      #248                // Field headerTextDisplay:Landroid/widget/TextView;
       4: ifnull        26
       7: aload_0
       8: getfield      #248                // Field headerTextDisplay:Landroid/widget/TextView;
      11: aload_1
      12: invokestatic  #416                // Method android/text/Html.fromHtml:(Ljava/lang/String;)Landroid/text/Spanned;
      15: invokevirtual #363                // Method android/widget/TextView.setText:(Ljava/lang/CharSequence;)V
      18: aload_0
      19: getfield      #263                // Field filterRelativeLayout:Landroid/widget/RelativeLayout;
      22: iconst_0
      23: invokevirtual #364                // Method android/widget/RelativeLayout.setVisibility:(I)V
      26: aload_0
      27: getfield      #255                // Field filterStatus:Landroid/widget/TextView;
      30: ifnull        73
      33: aload_0
      34: getfield      #255                // Field filterStatus:Landroid/widget/TextView;
      37: new           #422                // class java/lang/StringBuilder
      40: dup
      41: invokespecial #424                // Method java/lang/StringBuilder."<init>":()V
      44: aload_0
      45: getfield      #330                // Field clientAdapter:Lorg/smartregister/cursoradapter/RecyclerViewPaginatedAdapter;
      48: invokevirtual #334                // Method org/smartregister/cursoradapter/RecyclerViewPaginatedAdapter.getTotalcount:()I
      51: invokevirtual #425                // Method java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
      54: ldc_w         #429                // String  patients
      57: invokevirtual #431                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      60: aload_2
      61: invokevirtual #431                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      64: invokevirtual #434                // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
      67: invokestatic  #416                // Method android/text/Html.fromHtml:(Ljava/lang/String;)Landroid/text/Spanned;
      70: invokevirtual #363                // Method android/widget/TextView.setText:(Ljava/lang/CharSequence;)V
      73: return

  protected org.smartregister.provider.SmartRegisterClientsProvider clientsProvider();
    Code:
       0: aconst_null
       1: areturn

  protected void onInitialization();
    Code:
       0: return

  protected abstract void startRegistration();

  protected void onCreation();
    Code:
       0: aload_0
       1: invokevirtual #437                // Method initializePresenter:()V
       4: aload_0
       5: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
       8: invokevirtual #440                // Method androidx/fragment/app/FragmentActivity.getIntent:()Landroid/content/Intent;
      11: invokevirtual #441                // Method android/content/Intent.getExtras:()Landroid/os/Bundle;
      14: astore_1
      15: aload_1
      16: ifnull        40
      19: aload_1
      20: ldc_w         #447                // String is_remote_login
      23: invokevirtual #449                // Method android/os/Bundle.getBoolean:(Ljava/lang/String;)Z
      26: istore_2
      27: iload_2
      28: ifeq          40
      31: aload_0
      32: getfield      #212                // Field presenter:Lorg/smartregister/view/contract/BaseRegisterFragmentContract$Presenter;
      35: invokeinterface #455,  1          // InterfaceMethod org/smartregister/view/contract/BaseRegisterFragmentContract$Presenter.startSync:()V
      40: return

  public boolean onBackPressed();
    Code:
       0: iconst_0
       1: ireturn

  protected abstract void onViewClicked(android.view.View);

  public void onSyncInProgress(org.smartregister.domain.FetchStatus);
    Code:
       0: aload_0
       1: aload_1
       2: invokevirtual #471                // Method refreshSyncStatusViews:(Lorg/smartregister/domain/FetchStatus;)V
       5: return

  public void onSyncStart();
    Code:
       0: aload_0
       1: aconst_null
       2: invokevirtual #471                // Method refreshSyncStatusViews:(Lorg/smartregister/domain/FetchStatus;)V
       5: return

  public void onSyncComplete(org.smartregister.domain.FetchStatus);
    Code:
       0: aload_0
       1: aload_1
       2: invokevirtual #471                // Method refreshSyncStatusViews:(Lorg/smartregister/domain/FetchStatus;)V
       5: return

  protected void showShortToast(android.content.Context, java.lang.String);
    Code:
       0: aload_1
       1: aload_2
       2: invokestatic  #475                // Method org/smartregister/util/Utils.showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
       5: return

  protected void refreshSyncStatusViews(org.smartregister.domain.FetchStatus);
    Code:
       0: aload_0
       1: invokevirtual #479                // Method isSyncing:()Z
       4: ifeq          49
       7: aload_0
       8: aload_0
       9: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      12: aload_0
      13: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      16: getstatic     #482                // Field org/smartregister/R$string.syncing:I
      19: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
      22: invokevirtual #485                // Method showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
      25: aload_0
      26: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      29: getstatic     #482                // Field org/smartregister/R$string.syncing:I
      32: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
      35: iconst_0
      36: anewarray     #159                // class java/lang/Object
      39: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
      42: aload_0
      43: invokevirtual #324                // Method refreshSyncProgressSpinner:()V
      46: goto          353
      49: aload_1
      50: ifnull        339
      53: aload_1
      54: getstatic     #486                // Field org/smartregister/domain/FetchStatus.fetchedFailed:Lorg/smartregister/domain/FetchStatus;
      57: invokevirtual #492                // Method org/smartregister/domain/FetchStatus.equals:(Ljava/lang/Object;)Z
      60: ifeq          213
      63: aload_1
      64: invokevirtual #496                // Method org/smartregister/domain/FetchStatus.displayValue:()Ljava/lang/String;
      67: getstatic     #499                // Field org/smartregister/domain/ResponseErrorStatus.malformed_url:Lorg/smartregister/domain/ResponseErrorStatus;
      70: invokevirtual #505                // Method org/smartregister/domain/ResponseErrorStatus.name:()Ljava/lang/String;
      73: invokevirtual #508                // Method java/lang/String.equals:(Ljava/lang/Object;)Z
      76: ifeq          117
      79: aload_0
      80: aload_0
      81: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      84: aload_0
      85: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
      88: getstatic     #509                // Field org/smartregister/R$string.sync_failed_malformed_url:I
      91: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
      94: invokevirtual #485                // Method showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
      97: aload_0
      98: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     101: getstatic     #509                // Field org/smartregister/R$string.sync_failed_malformed_url:I
     104: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     107: iconst_0
     108: anewarray     #159                // class java/lang/Object
     111: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
     114: goto          206
     117: aload_1
     118: invokevirtual #496                // Method org/smartregister/domain/FetchStatus.displayValue:()Ljava/lang/String;
     121: getstatic     #512                // Field org/smartregister/domain/ResponseErrorStatus.timeout:Lorg/smartregister/domain/ResponseErrorStatus;
     124: invokevirtual #505                // Method org/smartregister/domain/ResponseErrorStatus.name:()Ljava/lang/String;
     127: invokevirtual #508                // Method java/lang/String.equals:(Ljava/lang/Object;)Z
     130: ifeq          171
     133: aload_0
     134: aload_0
     135: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     138: aload_0
     139: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     142: getstatic     #515                // Field org/smartregister/R$string.sync_failed_timeout_error:I
     145: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     148: invokevirtual #485                // Method showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
     151: aload_0
     152: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     155: getstatic     #515                // Field org/smartregister/R$string.sync_failed_timeout_error:I
     158: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     161: iconst_0
     162: anewarray     #159                // class java/lang/Object
     165: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
     168: goto          206
     171: aload_0
     172: aload_0
     173: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     176: aload_0
     177: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     180: getstatic     #518                // Field org/smartregister/R$string.sync_failed:I
     183: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     186: invokevirtual #485                // Method showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
     189: aload_0
     190: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     193: getstatic     #518                // Field org/smartregister/R$string.sync_failed:I
     196: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     199: iconst_0
     200: anewarray     #159                // class java/lang/Object
     203: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
     206: aload_0
     207: invokevirtual #324                // Method refreshSyncProgressSpinner:()V
     210: goto          353
     213: aload_1
     214: getstatic     #521                // Field org/smartregister/domain/FetchStatus.fetched:Lorg/smartregister/domain/FetchStatus;
     217: invokevirtual #492                // Method org/smartregister/domain/FetchStatus.equals:(Ljava/lang/Object;)Z
     220: ifne          233
     223: aload_1
     224: getstatic     #524                // Field org/smartregister/domain/FetchStatus.nothingFetched:Lorg/smartregister/domain/FetchStatus;
     227: invokevirtual #492                // Method org/smartregister/domain/FetchStatus.equals:(Ljava/lang/Object;)Z
     230: ifeq          280
     233: aload_0
     234: iconst_1
     235: invokevirtual #527                // Method setRefreshList:(Z)V
     238: aload_0
     239: invokevirtual #313                // Method renderView:()V
     242: aload_0
     243: aload_0
     244: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     247: aload_0
     248: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     251: getstatic     #530                // Field org/smartregister/R$string.sync_complete:I
     254: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     257: invokevirtual #485                // Method showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
     260: aload_0
     261: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     264: getstatic     #530                // Field org/smartregister/R$string.sync_complete:I
     267: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     270: iconst_0
     271: anewarray     #159                // class java/lang/Object
     274: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
     277: goto          353
     280: aload_1
     281: getstatic     #533                // Field org/smartregister/domain/FetchStatus.noConnection:Lorg/smartregister/domain/FetchStatus;
     284: invokevirtual #492                // Method org/smartregister/domain/FetchStatus.equals:(Ljava/lang/Object;)Z
     287: ifeq          332
     290: aload_0
     291: aload_0
     292: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     295: aload_0
     296: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     299: getstatic     #536                // Field org/smartregister/R$string.sync_failed_no_internet:I
     302: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     305: invokevirtual #485                // Method showShortToast:(Landroid/content/Context;Ljava/lang/String;)V
     308: aload_0
     309: invokevirtual #62                 // Method getActivity:()Landroidx/fragment/app/FragmentActivity;
     312: getstatic     #536                // Field org/smartregister/R$string.sync_failed_no_internet:I
     315: invokevirtual #344                // Method androidx/fragment/app/FragmentActivity.getString:(I)Ljava/lang/String;
     318: iconst_0
     319: anewarray     #159                // class java/lang/Object
     322: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
     325: aload_0
     326: invokevirtual #324                // Method refreshSyncProgressSpinner:()V
     329: goto          353
     332: aload_0
     333: invokevirtual #324                // Method refreshSyncProgressSpinner:()V
     336: goto          353
     339: ldc_w         #539                // String Fetch Status NULL
     342: iconst_0
     343: anewarray     #159                // class java/lang/Object
     346: invokestatic  #161                // Method timber/log/Timber.i:(Ljava/lang/String;[Ljava/lang/Object;)V
     349: aload_0
     350: invokevirtual #324                // Method refreshSyncProgressSpinner:()V
     353: return

  protected boolean isSyncing();
    Code:
       0: invokestatic  #458                // Method org/smartregister/receiver/SyncStatusBroadcastReceiver.getInstance:()Lorg/smartregister/receiver/SyncStatusBroadcastReceiver;
       3: invokevirtual #541                // Method org/smartregister/receiver/SyncStatusBroadcastReceiver.isSyncing:()Z
       6: ireturn

  public void onResume();
    Code:
       0: aload_0
       1: invokespecial #542                // Method org/smartregister/cursoradapter/RecyclerViewFragment.onResume:()V
       4: aload_0
       5: invokespecial #545                // Method registerSyncStatusBroadcastReceiver:()V
       8: return

  public void onPause();
    Code:
       0: aload_0
       1: invokespecial #548                // Method unregisterSyncStatusBroadcastReceiver:()V
       4: aload_0
       5: invokespecial #551                // Method org/smartregister/cursoradapter/RecyclerViewFragment.onPause:()V
       8: return

  protected void refreshSyncProgressSpinner();
    Code:
       0: aload_0
       1: invokevirtual #479                // Method isSyncing:()Z
       4: ifeq          41
       7: aload_0
       8: getfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      11: ifnull        22
      14: aload_0
      15: getfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      18: iconst_0
      19: invokevirtual #209                // Method android/widget/ProgressBar.setVisibility:(I)V
      22: aload_0
      23: getfield      #295                // Field syncButton:Landroid/widget/ImageView;
      26: ifnull        72
      29: aload_0
      30: getfield      #295                // Field syncButton:Landroid/widget/ImageView;
      33: bipush        8
      35: invokevirtual #554                // Method android/widget/ImageView.setVisibility:(I)V
      38: goto          72
      41: aload_0
      42: getfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      45: ifnull        57
      48: aload_0
      49: getfield      #281                // Field syncProgressBar:Landroid/widget/ProgressBar;
      52: bipush        8
      54: invokevirtual #209                // Method android/widget/ProgressBar.setVisibility:(I)V
      57: aload_0
      58: getfield      #295                // Field syncButton:Landroid/widget/ImageView;
      61: ifnull        72
      64: aload_0
      65: getfield      #295                // Field syncButton:Landroid/widget/ImageView;
      68: iconst_0
      69: invokevirtual #554                // Method android/widget/ImageView.setVisibility:(I)V
      72: return

  static {};
    Code:
       0: ldc           #13                 // class org/smartregister/view/fragment/BaseRegisterFragment
       2: invokevirtual #570                // Method java/lang/Class.getCanonicalName:()Ljava/lang/String;
       5: putstatic     #575                // Field TAG:Ljava/lang/String;
       8: new           #422                // class java/lang/StringBuilder
      11: dup
      12: invokespecial #424                // Method java/lang/StringBuilder."<init>":()V
      15: ldc_w         #555                // class org/smartregister/view/activity/BaseRegisterActivity
      18: invokevirtual #578                // Method java/lang/Class.getPackage:()Ljava/lang/Package;
      21: invokevirtual #582                // Method java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      24: ldc_w         #585                // String .toolbarTitle
      27: invokevirtual #431                // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
      30: invokevirtual #434                // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
      33: putstatic     #94                 // Field TOOLBAR_TITLE:Ljava/lang/String;
      36: return
}

