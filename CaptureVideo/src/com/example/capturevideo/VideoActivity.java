package com.example.capturevideo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

public class VideoActivity extends FragmentActivity {

	private static String path;
	private static final String TAG = VideoActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * @param
	 * @return return combine video path string
	 * */
	private static String appendVideo(String[] videos) throws IOException {
		Log.v(TAG, "in appendVideo() videos length is " + videos.length);
		Movie[] inMovies = new Movie[videos.length];
		int index = 0;
		for (String video : videos) {
			Log.i(TAG, "    in appendVideo one video path = " + video);
			inMovies[index] = MovieCreator.build(video);
			index++;
		}
		List<Track> videoTracks = new LinkedList<Track>();
		List<Track> audioTracks = new LinkedList<Track>();
		for (Movie m : inMovies) {
			for (Track t : m.getTracks()) {
				if (t.getHandler().equals("soun")) {
					audioTracks.add(t);
				}
				if (t.getHandler().equals("vide")) {
					videoTracks.add(t);
				}
			}
		}

		Movie result = new Movie();
		Log.v(TAG, "audioTracks size = " + audioTracks.size()
				+ " videoTracks size = " + videoTracks.size());
		if (audioTracks.size() > 0) {
			result.addTrack(new AppendTrack(audioTracks
					.toArray(new Track[audioTracks.size()])));
		}
		if (videoTracks.size() > 0) {
			result.addTrack(new AppendTrack(videoTracks
					.toArray(new Track[videoTracks.size()])));
		}
		String videoCombinePath = "/sdcard/temp.mp4";
		Container out = new DefaultMp4Builder().build(result);
		FileChannel fc = new RandomAccessFile(videoCombinePath, "rw")
				.getChannel();
		out.writeContainer(fc);
		fc.close();
		Log.v(TAG, "after combine videoCombinepath = " + videoCombinePath);
		return videoCombinePath;
	}

	static class FixVideos extends AsyncTask<Void, Void, String> {

		ProgressDialog mProgressDialog;
		Context context;

		public FixVideos(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(Void... params) {

			File mFile = new File("/sdcard/tempVideo/");

			if (!mFile.exists()) {
				return null;
			}

			File[] mFiles = mFile.listFiles();

			String[] videos = new String[mFiles.length];

			for (int i = 0; i < mFiles.length; i++) {
				videos[i] = mFiles[i].getAbsolutePath();
			}

			try {
				return appendVideo(videos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.cancel();
			path = result;

		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private TextView appendVideoTv;

		public PlaceholderFragment() {
		}

		@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			final VideoView mVideoView = (VideoView) rootView
					.findViewById(R.id.video);

			TextView mTextPlay = (TextView) rootView
					.findViewById(R.id.text_play);
			mTextPlay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!TextUtils.isEmpty(path)) {
						mVideoView.setVideoPath(path);
						mVideoView.start();
					}

				}
			});

			TextView mTextNext = (TextView) rootView
					.findViewById(R.id.text_next);
			mTextNext.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent mIntent = new Intent(getActivity(),
							NextActivity.class);
					mIntent.putExtra(NextActivity.KEY_PATH, path);
					startActivity(mIntent);
					getActivity().finish();

				}
			});

			FixVideos mFixVideos = new FixVideos(getActivity());
			mFixVideos.execute();

			return rootView;
		}
	}

}
