ALTER TABLE evaluation_results
ADD COLUMN IF NOT EXISTS technical_score INT CHECK (technical_score >= 0 AND technical_score <= 100),
ADD COLUMN IF NOT EXISTS communication_score INT CHECK (communication_score >= 0 AND communication_score <= 100),
ADD COLUMN IF NOT EXISTS problem_solving_score INT CHECK (problem_solving_score >= 0 AND problem_solving_score <= 100),
ADD COLUMN IF NOT EXISTS strengths TEXT,
ADD COLUMN IF NOT EXISTS weaknesses TEXT,
ADD COLUMN IF NOT EXISTS ai_feedback TEXT;

