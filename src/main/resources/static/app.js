const startButton = document.getElementById("startButton");
const statusText = document.getElementById("status");
const stopButton = document.getElementById("stopButton");
const transcription = document.getElementById("transcription");
// Browser's audio recorder
let mediaRecorder;
// Store audio 
let audioArray = [];

startButton.addEventListener("click", async() => {
	try {
		// Ask browser for microphone access
		const stream = navigator.mediaDevices.getUserMedia({
		    audio: true
		});
		
		console.log("Microphone access granted!");
	} catch(error){
		console.log("Microphone access denied: ", error);
		statusText.innerText = "Microphone inaccessible";
	}
})