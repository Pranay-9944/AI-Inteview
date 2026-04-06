from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import requests

app = Flask(__name__)
CORS(app)

GROQ_API_KEY = os.environ.get("GROQ_API_KEY", "your_groq_api_key_here")
GROQ_URL     = "https://api.groq.com/openai/v1/chat/completions"
GROQ_MODEL   = "llama3-8b-8192"


def call_groq(system_prompt: str, user_message: str) -> str:
    headers = {
        "Authorization": f"Bearer {GROQ_API_KEY}",
        "Content-Type":  "application/json",
    }
    body = {
        "model":       GROQ_MODEL,
        "temperature": 0.3,
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user",   "content": user_message},
        ],
    }
    resp = requests.post(GROQ_URL, headers=headers, json=body, timeout=30)
    resp.raise_for_status()
    return resp.json()["choices"][0]["message"]["content"].strip()


# ── 1. Evaluate a candidate answer ─────────────────────────────────────────────
@app.route("/evaluate", methods=["POST"])
def evaluate():
    data      = request.json or {}
    answer    = data.get("answer", "").strip()
    question  = data.get("question", "")
    round_type = data.get("type", "AI")   # AI | HR | DSA

    if not answer:
        return jsonify({"score": 0, "feedback": "No answer provided."}), 400

    system = (
        "You are a strict but fair technical interviewer. "
        "Evaluate the candidate's answer and return ONLY valid JSON with two keys: "
        '{"score": <integer 0-10>, "feedback": "<one concise sentence>"}. '
        "No markdown, no explanation outside the JSON."
    )
    user_msg = (
        f"Interview round: {round_type}\n"
        f"Question: {question}\n"
        f"Candidate answer: {answer}"
    )

    try:
        raw = call_groq(system, user_msg)
        import json, re
        # Strip any accidental code fences
        raw = re.sub(r"```[a-z]*", "", raw).strip().strip("`").strip()
        result = json.loads(raw)
        score    = max(0, min(10, int(result.get("score", 5))))
        feedback = result.get("feedback", "No feedback.")
        return jsonify({"score": score, "feedback": feedback})
    except Exception as e:
        return jsonify({"score": 0, "feedback": f"AI evaluation failed: {str(e)}"}), 500


# ── 2. Generate interview questions from resume text ────────────────────────────
@app.route("/generate-questions", methods=["POST"])
def generate_questions():
    data       = request.json or {}
    resume_text = data.get("resumeText", "")
    round_type  = data.get("type", "AI")   # AI | HR | DSA
    count       = int(data.get("count", 5))

    system = (
        "You are an expert interviewer. Given a candidate's resume text, "
        f"generate exactly {count} {round_type} interview questions. "
        'Return ONLY a JSON array of strings like ["Q1", "Q2", ...]. '
        "No markdown, no explanation, no numbering outside the array."
    )
    user_msg = f"Resume:\n{resume_text[:3000]}"   # cap to avoid token overflow

    try:
        raw = call_groq(system, user_msg)
        import json, re
        raw = re.sub(r"```[a-z]*", "", raw).strip().strip("`").strip()
        questions = json.loads(raw)
        if not isinstance(questions, list):
            raise ValueError("Not a list")
        return jsonify({"questions": questions[:count]})
    except Exception as e:
        # Fallback generic questions
        fallback = [
            "Tell me about yourself.",
            "What is your greatest technical strength?",
            "Describe a challenging project you worked on.",
            "How do you handle tight deadlines?",
            "Where do you see yourself in 5 years?",
        ]
        return jsonify({"questions": fallback[:count], "warning": str(e)})


# ── 3. Health check ─────────────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)


# ── 4. Parse resume into structured fields ──────────────────────────────────
@app.route("/parse-resume", methods=["POST"])
def parse_resume():
    data        = request.json or {}
    resume_text = data.get("resumeText", "")

    system = (
        "You are a resume parser. Extract fields from the resume and return ONLY valid JSON. "
        "No markdown, no code fences, no explanation. "
        'Schema: {"jobTitle": "string", "experienceYears": "string", "skills": ["skill1","skill2",...]}'
    )
    user_msg = f"Resume:\n{resume_text[:3000]}"

    try:
        raw = call_groq(system, user_msg)
        import json, re
        raw = re.sub(r"```[a-z]*", "", raw).strip().strip("`").strip()
        result = json.loads(raw)
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500