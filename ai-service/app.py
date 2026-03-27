from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/evaluate', methods=['POST'])
def evaluate():
    data = request.json
    answer = data['answer']

    # Simple AI logic (replace later with real AI)
    score = min(len(answer) // 10, 10)

    if score >= 8:
        feedback = "Excellent answer"
    elif score >= 5:
        feedback = "Good but can improve"
    else:
        feedback = "Answer too short"

    return jsonify({
        "score": score,
        "feedback": feedback
    })

app.run(host="0.0.0.0", port=5000)