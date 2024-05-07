onload = () => {
    getRoom();
    createRoom();
}

const getRoom = () => {

    new asyncConn().post("/room/create", null, (result)=>{
        createChattingRoom(result);
    });
}

const createRoom = () => {
    document.querySelector("#createRoom").onclick = () => {
        const msg = {
            roomName: document.querySelector("#roomName").value
        };

        new asyncConn().post("/room/create", msg, (result)=>{
            createChattingRoom(result);
        });

        document.querySelector("#roomName").value = '';
    };
}

const createChattingRoom = (list) => {

    if (list != null) {
        let tag = "<tr><th class='num'>순서</th><th class='room'>방 이름</th><th class='go'></th></tr>";
        list.forEach((r, idx) => {
            const rName = r.roomName.trim();
            const rNumber = r.roomNum;

            tag += "<tr>"
                    + "<td class='num'>" + (idx + 1) + "</td>"
                    + "<td class='room'>" + rName + "</td>"
                    + "<td class='go'><input type='button' onclick='goRoom(" + rNumber + ", \"" + rName + "\")' value='참여'></td>"
                + "</tr>";
        });

        document.querySelector("#roomList").innerHTML = tag;
    }
}

const goRoom = (number, name) => {
    location.href = '/move/chatting?roomName=' + name + "&roomNumber=" + number;
}

class asyncConn {
	constructor() {
		this.request = new XMLHttpRequest();
	}

	get(url, callback) {

		this.#send("GET", url, null, callback);
	}

	post(url, data, callback) {

		this.#send("POST", url, data, callback)
	}

	#send(type, url, data, callback) {
		const req = this.request;
		req.open(type, url, true);

		req.onreadystatechange = (event) => {
			console.log(req.status, req.readyState);
			if (req.status == 200 && req.readyState == XMLHttpRequest.DONE) {
			    let result = null;

			    try {
			        result = JSON.parse(req.responseText);
			    } catch (e) {
			        console.error(e);
			    }

				callback(result);
			} else if (400 <= req.status && req.status < 600 ) {
				callback(req.responseText);
			}
		}

        if (data == null)
		    req.send()
		else {
		    req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		    console.log(data, type);

		    let params = "";
		    Object.keys(data).forEach((k)=>{
                params += `${k}=${data[k]}&`;
            })

		    req.send(params);
		}
	}
}