let ws;

const wsOpen = () => {
    ws = new WebSocket("ws://" + location.host + "/chatting/" + document.querySelector("#roomNumber").value);
    wsEvt();
}

const wsEvt = () => {
    ws.onopen = (data) => {
        //소켓이 열리면 초기화 세팅하기
    }

    ws.onmessage = (data) => {
        var msg = data.data;

        console.log(msg)
        if(msg != null && msg.trim() != ''){
            const d = JSON.parse(msg);
            const sidEle = document.querySelector("#sessionId");

            if (d.type == 'getId') {
                const si = d.sessionId != null ? d.sessionId : "";

                if (si != '') {
                    sidEle.value = si;
                }
            } else if (d.type == 'message') {
                const pEle = document.createElement("p");

                if (d.sessionId == sidEle.value) {
                    pEle.classList.add('me');
                    pEle.innerText = '나 : ' + d.msg;
                } else {
                    pEle.classList.add('others');
                    pEle.innerText = d.userName + ' : ' + d.msg;
                }

                document.querySelector("#chating").append(pEle);
            } else {
                console.warn('unknown type!');
            }
        }
    }

    document.addEventListener("keypress", (e) => {
        if(e.keyCode == 13){ //enter press
            send();
        }
    });
}

const chatName = () => {
    const userName = document.querySelector("#userName").value;
    if(userName == null || userName.trim() == ""){
        alert("사용자 이름을 입력해주세요.");
        document.querySelector("#userName").focus();
    }else{
        wsOpen();
        document.querySelector("#yourName").style.display = "none";
        document.querySelector("#yourMsg").style.display = "inline-block";
    }
}

const send = () => {
    const option = {
        type: 'message',
        roomNumber: document.querySelector("#roomNumber").value,
        sessionId: document.querySelector('#sessionId').value,
        userName: document.querySelector('#userName').value,
        msg: document.querySelector('#chatting').value
    }

    ws.send(JSON.stringify(option));
    document.querySelector('#chatting').value = "";
}
