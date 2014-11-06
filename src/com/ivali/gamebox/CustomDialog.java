package com.ivali.gamebox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ivali.gamebox.R;


public class CustomDialog extends Dialog {
	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}
	public static TextView content,titleTextView;
	public static ProgressBar bar;
	public static Button ok,cancel;
	public static GridView grid;
	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private int layoutId = 0;
		private Context context;
		private String title;
		private String message;
		private boolean isShowButton_ok;
		private boolean isShowButton_cancel;
		private boolean isShowTextView_title;
		private String positiveButtonText;
		private String negativeButtonText;
		private boolean  flag;//进度条是否可见
		private SimpleAdapter adapter;
		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;
		private AdapterView.OnItemClickListener gridOnItemClickListener;
		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(boolean flag, String title) {
			this.title = title;
			this.isShowTextView_title = flag;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(boolean flag,
				DialogInterface.OnClickListener listener) {
			this.isShowButton_ok = flag;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				boolean flag, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.isShowButton_ok = flag;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(boolean flag,
				DialogInterface.OnClickListener listener) {
			this.isShowButton_cancel = flag;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				boolean flag, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.isShowButton_cancel = flag;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setLayoutResources(int layoutId) {
			this.layoutId = layoutId;
			return this;
		}

		public Builder setProgressBarVisiable(boolean  flag) {
			this.flag = flag;
			return this;
		}
		
		public Builder setGridViewAdapter(SimpleAdapter adapter) {
			this.adapter = adapter;
			return this;
		}
		
		public Builder setGridOnItemClickListener (AdapterView.OnItemClickListener gridOnItemClickListener) {
			this.gridOnItemClickListener = gridOnItemClickListener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.dialog);
			View layout = inflater.inflate(layoutId, null);
			// set the dialog title
			titleTextView=((TextView) layout.findViewById(R.id.custom_dialog_title));
			if (isShowTextView_title) {
				titleTextView.setText(title);
			} else {
				titleTextView.setVisibility(View.GONE);
			}
			// set the confirm button
						ok = (Button) layout
								.findViewById(R.id.custom_dialog_ok);
						if (isShowButton_ok) {
							if (positiveButtonClickListener != null) {
								
								if (this.positiveButtonText != null)
									ok.setText(positiveButtonText);
								ok.setOnClickListener(new View.OnClickListener() {
									public void onClick(View v) {
										positiveButtonClickListener.onClick(dialog,
												DialogInterface.BUTTON_POSITIVE);
									}
								});
							}
						} else {
							// if no confirm button just set the visibility to GONE
							ok.setVisibility(View.GONE);
						}

						// set the cancel button
						cancel = (Button) layout
								.findViewById(R.id.custom_dialog_cancel);
						if (isShowButton_cancel) {
							if (negativeButtonClickListener != null) {		
								if (this.negativeButtonText != null)
									cancel.setText(negativeButtonText);
								cancel.setOnClickListener(new View.OnClickListener() {
									public void onClick(View v) {
										negativeButtonClickListener.onClick(dialog,
												DialogInterface.BUTTON_NEGATIVE);
									}
								});
							}
						} else {
							// if no confirm button just set the visibility to GONE
							cancel.setVisibility(View.GONE);
						}
			// set the gridView
			grid=(GridView) layout.findViewById(R.id.custom_dialog_GridView);
			grid.setAdapter(adapter);
			grid.setOnItemClickListener(gridOnItemClickListener);
			// set the content message
//			content=(TextView) (layout.findViewById(R.id.custom_dialog_content));
//			content	.setText(message);
		
			
			dialog.setCancelable(false);
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
