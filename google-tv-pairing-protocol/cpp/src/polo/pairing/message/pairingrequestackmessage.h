// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#ifndef POLO_PAIRING_MESSAGE_PAIRINGREQUESTACKMESSAGE_H_
#define POLO_PAIRING_MESSAGE_PAIRINGREQUESTACKMESSAGE_H_

#include <string>
#include "polo/pairing/message/polomessage.h"

namespace polo {
namespace pairing {
namespace message {

// Ack for a pairing request message.
class PairingRequestAckMessage : public PoloMessage {
 public:
  // Creates a new pairing request ack with no server name.
  PairingRequestAckMessage();

  // Creates a new pairing request ack with the given server name.
  // @param server_name the server name
  explicit PairingRequestAckMessage(const std::string& server_name);

  // Gets the server name.
  std::string server_name() const;

  // Gets whether there is a server name.
  bool has_server_name() const;

  // @override
  virtual std::string ToString() const;
 private:
  std::string server_name_;

  DISALLOW_COPY_AND_ASSIGN(PairingRequestAckMessage);
};

}  // namespace message
}  // namespace pairing
}  // namespace polo

#endif  // POLO_PAIRING_MESSAGE_PAIRINGREQUESTACKMESSAGE_H_
