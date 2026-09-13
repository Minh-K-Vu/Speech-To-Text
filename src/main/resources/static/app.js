const startButton = document.getElementById("startButton");
const statusText = document.getElementById("status");
const stopButton = document.getElementById("stopButton");
const transcription = document.getElementById("transcription");
// Browser's audio recorder
let mediaRecorder;
// Store audio 
let audioArrays = [];

startButton.addEventListener("click", async() => {
	try {
		// Ask browser for microphone access
		const stream = await navigator.mediaDevices.getUserMedia({
		    audio: true
		});
		// Clear audio array
		audioArrays = [];
		
		// Use microphone stream for recorder
		mediaRecorder = new MediaRecorder(stream);
		// Record audio
		mediaRecorder.addEventListener("dataavailable", event => {
			audioArrays.push(event.data);
		})
		
		// When audio stop
		mediaRecorder.addEventListener("stop", () => {
			const combinedAudio = new Blob(
				audioArrays,
				{
					type: mediaRecorder.mimeType
				}
			);
			console.log("Recording stopped.");
			console.log("Audio size: ", combinedAudio.size);
			//Update statusText
			statusText.innerText = "Recording stopped."
			//Update button status
			startButton.disabled = false;
			stopButton.disabled = true;
			sendAudioToServer(combinedAudio);
		})
		// Start recording
		mediaRecorder.start();
		// Update statusText
		statusText.innerText = "Recording...";
		
		// Update button status
		startButton.disabled = true;
		stopButton.disabled = false;
		
		console.log("Microphone access granted!");
		
		
		
	} catch(error){
		console.log("Microphone access denied: ", error);
		statusText.innerText = "Microphone inaccessible";
	}
})

stopButton.addEventListener("click", async() => {
	//stop if media recorder is running
	if (
	    mediaRecorder &&
	    mediaRecorder.state === "recording"
	) {

	    mediaRecorder.stop();
		
		
	    statusText.innerText =
	        "Processing recording...";

	    stopButton.disabled = true;
	}
})

async function sendAudioToServer(blob){
	// send files with http
	const formData = new FormData()
	
	formData.append(
		"file",
		blob,
		"recording.webm"
	)
	
	try {
		// Post request
		const response = await fetch(
			"/api/speechtotext/transcribe",
			{
			    method: "POST",
			    body: formData
			}
		)
		// Get response from java
		const result = await response.text();

		console.log("Java:", result);

		transcription.innerText = result;
	} catch (error) {
		console.log(error);
	}
}