# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20091023211955) do

  create_table "clients", :force => true do |t|
    t.string   "name"
    t.integer  "contestant_id"
    t.integer  "author_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "contestants", :force => true do |t|
    t.string   "name"
    t.integer  "contest_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "score_id"
  end

  create_table "contests", :force => true do |t|
    t.string   "name"
    t.boolean  "active"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "match_score_definition_id"
    t.integer  "round_score_definition_id"
    t.integer  "rounds_per_match",            :default => 1
    t.text     "script_to_aggregate_rounds"
    t.text     "script_to_aggregate_matches"
  end

  create_table "delayed_jobs", :force => true do |t|
    t.integer  "priority",   :default => 0
    t.integer  "attempts",   :default => 0
    t.text     "handler"
    t.string   "last_error"
    t.datetime "run_at"
    t.datetime "locked_at"
    t.datetime "failed_at"
    t.string   "locked_by"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "match_slots", :force => true do |t|
    t.string   "contestant_type"
    t.integer  "contestant_id"
    t.integer  "match_id"
    t.integer  "position"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "score_id"
  end

  create_table "matchdays", :force => true do |t|
    t.integer  "contest_id"
    t.date     "when"
    t.integer  "position"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.datetime "played_at"
    t.integer  "job_id"
  end

  create_table "matches", :force => true do |t|
    t.integer  "set_id"
    t.string   "set_type"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.datetime "played_at"
    t.integer  "job_id"
  end

  create_table "memberships", :force => true do |t|
    t.integer  "person_id"
    t.integer  "contestant_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "tutor",         :default => false, :null => false
    t.boolean  "teacher",       :default => false, :null => false
  end

  create_table "people", :force => true do |t|
    t.string   "email"
    t.string   "password_hash"
    t.string   "password_salt"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "administrator",        :default => false, :null => false
    t.boolean  "blocked",              :default => false, :null => false
    t.string   "first_name",           :default => "",    :null => false
    t.string   "last_name",            :default => "",    :null => false
    t.string   "nick_name",            :default => "",    :null => false
    t.boolean  "show_email_to_others", :default => false, :null => false
  end

  create_table "round_slots", :force => true do |t|
    t.integer  "match_slot_id"
    t.integer  "round_id"
    t.integer  "score_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "position"
  end

  create_table "rounds", :force => true do |t|
    t.integer  "match_id"
    t.datetime "played_at"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "score_definition_fragments", :force => true do |t|
    t.string  "name"
    t.integer "definition_id"
    t.boolean "main",          :default => false, :null => false
    t.integer "position"
  end

  create_table "score_definitions", :force => true do |t|
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "score_fragments", :force => true do |t|
    t.integer "definition_id"
    t.integer "score_id"
    t.integer "value"
  end

  create_table "scores", :force => true do |t|
    t.integer  "definition_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
